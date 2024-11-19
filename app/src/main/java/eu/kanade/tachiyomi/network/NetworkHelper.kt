package eu.kanade.tachiyomi.network

import android.content.Context
import android.os.Build
import ani.dantotsu.Mapper
import ani.dantotsu.settings.saving.PrefManager
import ani.dantotsu.settings.saving.PrefName
import ani.dantotsu.util.Logger
import com.lagradost.nicehttp.Requests
import eu.kanade.tachiyomi.network.interceptor.CloudflareInterceptor
import eu.kanade.tachiyomi.network.interceptor.IgnoreGzipInterceptor
import eu.kanade.tachiyomi.network.interceptor.UncaughtExceptionInterceptor
import eu.kanade.tachiyomi.network.interceptor.UserAgentInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.brotli.BrotliInterceptor
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import java.net.InetSocketAddress
import java.net.Proxy
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.Response
import okhttp3.Route

class NetworkHelper(
    context: Context
) {

    val cookieJar = AndroidCookieJar()

    val client: OkHttpClient = run {
        val builder = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .callTimeout(2, TimeUnit.MINUTES)
            .cache(
                Cache(
                    directory = File(context.externalCacheDir ?: context.cacheDir, "network_cache"),
                    maxSize = 5L * 1024 * 1024, // 5 MiB
                ),
            )
            .addInterceptor(UncaughtExceptionInterceptor())
            .addInterceptor(UserAgentInterceptor(::defaultUserAgentProvider))
            .addNetworkInterceptor(IgnoreGzipInterceptor())
            .addNetworkInterceptor(BrotliInterceptor)

        class ConsoleLogger : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Logger.log("OkHttp: $message")
            }
        }

        if (PrefManager.getVal<Boolean>(PrefName.VerboseLogging)) {
            val httpLoggingInterceptor = HttpLoggingInterceptor(ConsoleLogger()).apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            builder.addNetworkInterceptor(httpLoggingInterceptor)
        }

        builder.addInterceptor(
            CloudflareInterceptor(context, cookieJar, ::defaultUserAgentProvider),
        )

        when (PrefManager.getVal<Int>(PrefName.DohProvider)) {
            PREF_DOH_CLOUDFLARE -> builder.dohCloudflare()
            PREF_DOH_GOOGLE -> builder.dohGoogle()
            PREF_DOH_ADGUARD -> builder.dohAdGuard()
            PREF_DOH_QUAD9 -> builder.dohQuad9()
            PREF_DOH_ALIDNS -> builder.dohAliDNS()
            PREF_DOH_DNSPOD -> builder.dohDNSPod()
            PREF_DOH_360 -> builder.doh360()
            PREF_DOH_QUAD101 -> builder.dohQuad101()
            PREF_DOH_MULLVAD -> builder.dohMullvad()
            PREF_DOH_CONTROLD -> builder.dohControlD()
            PREF_DOH_NJALLA -> builder.dohNajalla()
            PREF_DOH_SHECAN -> builder.dohShecan()
            PREF_DOH_LIBREDNS -> builder.dohLibreDNS()
        }

        // Add SOCKS5 proxy if enabled
        if (PrefManager.getVal<Boolean>(PrefName.EnableSocks5Proxy)) {
            val proxyHost = PrefManager.getVal<String>(PrefName.Socks5ProxyHost)
            val proxyPort = PrefManager.getVal<Int>(PrefName.Socks5ProxyPort)
            val proxyUsername = PrefManager.getVal<String>(PrefName.Socks5ProxyUsername)
            val proxyPassword = PrefManager.getVal<String>(PrefName.Socks5ProxyPassword)
        
            // Set up the proxy
            val proxy = Proxy(Proxy.Type.SOCKS, InetSocketAddress(proxyHost, proxyPort))
            builder.proxy(proxy)
        
            // Only set proxy authenticator if both username and password are provided
            if (!proxyUsername.isNullOrEmpty() && !proxyPassword.isNullOrEmpty()) {
                val authenticator = Authenticator { _: Route?, response: Response ->
                    val credential = Credentials.basic(proxyUsername, proxyPassword)
                    response.request.newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build()
                }
                builder.proxyAuthenticator(authenticator)
            }
        }
        builder.build()
    }

    val downloadClient = client.newBuilder().callTimeout(20, TimeUnit.MINUTES).build()

    /**
     * @deprecated Since extension-lib 1.5
     */
    @Deprecated("The regular client handles Cloudflare by default")
    @Suppress("UNUSED")
    val cloudflareClient: OkHttpClient = client

    val requestClient = Requests(
        client,
        mapOf(
            "User-Agent" to
                    defaultUserAgentProvider()
                        .format(Build.VERSION.RELEASE, Build.MODEL)
        ),
        defaultCacheTime = 6,
        defaultCacheTimeUnit = TimeUnit.HOURS,
        responseParser = Mapper
    )

    companion object {
        fun defaultUserAgentProvider() = PrefManager.getVal<String>(PrefName.DefaultUserAgent)
    }
}