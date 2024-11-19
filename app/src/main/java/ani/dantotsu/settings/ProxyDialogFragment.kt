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
import ani.dantotsu.restartApp

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
        binding.proxyPort.setText(if (proxyPort == 0) "" else proxyPort.toString()) // Display empty if 0
        binding.proxyUsername.setText(proxyUsername)
        binding.proxyPassword.setText(proxyPassword)
        binding.proxyAuthentication.isChecked = authEnabled

        // Save Button
        binding.proxySave.setOnClickListener {
            proxyHost = binding.proxyHost.text.toString() ?: ""

            // Parse port, treat blank or invalid as 0
            val portInput = binding.proxyPort.text.toString()
            proxyPort = portInput.toIntOrNull() ?: 0

            proxyUsername = binding.proxyUsername.text.toString() ?: ""
            proxyPassword = binding.proxyPassword.text.toString() ?: ""

            // Save to PrefManager
            PrefManager.setVal(PrefName.Socks5ProxyHost, proxyHost)
            PrefManager.setVal(PrefName.Socks5ProxyPort, proxyPort) // Save 0 if blank
            PrefManager.setVal(PrefName.Socks5ProxyUsername, proxyUsername)
            PrefManager.setVal(PrefName.Socks5ProxyPassword, proxyPassword)

            dismiss()
            //activity?.restartApp()
        }

        // MaterialCheckBox
        binding.proxyAuthentication.setOnCheckedChangeListener { _, isChecked ->
            PrefManager.setVal(PrefName.ProxyAuthEnabled, isChecked)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}