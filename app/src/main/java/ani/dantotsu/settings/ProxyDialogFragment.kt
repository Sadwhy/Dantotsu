package ani.dantotsu.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ani.dantotsu.BottomSheetDialogFragment
import ani.dantotsu.databinding.BottomSheetProxyBinding
import ani.dantotsu.snackString
import ani.dantotsu.settings.saving.PrefManager
import ani.dantotsu.settings.saving.PrefName

class ProxyDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetProxyBinding? = null
    private val binding get() = _binding!!

    private var proxyHost: String? = PrefManager.getVal<String>(PrefName.Socks5ProxyHost) ?: ""
    private var proxyPort: Int = PrefManager.getVal<Int>(PrefName.Socks5ProxyPort) ?: 0
    private var proxyUsername: String? = PrefManager.getVal<String>(PrefName.Socks5ProxyUsername) ?: ""
    private var proxyPassword: String? = PrefManager.getVal<String>(PrefName.Socks5ProxyPassword) ?: ""
    private var authEnabled: Boolean = PrefManager.getVal<Boolean>(PrefName.ProxyAuthEnabled)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetProxyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.proxyHost.setText(proxyHost)
        binding.proxyPort.setText(proxyPort.toString()) // Ensure port is a string
        binding.proxyUsername.setText(proxyUsername)
        binding.proxyPassword.setText(proxyPassword)
        binding.proxyAuthentication.isChecked = authEnabled

        // Save Button
        binding.proxySave.setOnClickListener {
            proxyHost = binding.proxyHost.text.toString()
            proxyPort = binding.proxyPort.text.toString().toInt()
            proxyUsername = binding.proxyUsername.text.toString()
            proxyPassword = binding.proxyPassword.text.toString()

            val combinedString = """
                Host: $proxyHost, Port: $proxyPort,
                Username: $proxyUsername, Password: $proxyPassword,
                Authentication: $authEnabled
            """.trimIndent()

            snackString(combinedString)
        }

        // MaterialCheckBox
        binding.proxyAuthentication.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
              PrefManager.setVal(PrefName.ProxyAuthEnabled, true)
            } else {
              PrefManager.setVal(PrefName.ProxyAuthEnabled, false) 
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}