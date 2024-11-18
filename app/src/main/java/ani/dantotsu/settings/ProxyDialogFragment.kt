package ani.dantotsu.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ani.dantotsu.BottomSheetDialogFragment
import ani.dantotsu.databinding.BottomSheetProxyBinding
import ani.dantotsu.snackString

class ProxyDialogFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetProxyBinding? = null
    private val binding get() = _binding!!


    private var proxyHost: String? = null
    private var proxyPort: Int = 0 // Default value set to 0
    private var proxyUsername: String? = null
    private var proxyPassword: String? = null
    private var isAuthenticationChecked: Boolean = false // To store checkbox state

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

        // Set listener for the save button
        binding.proxySave.setOnClickListener {
            proxyHost = binding.proxyHost.text.toString()

            // Convert proxyPort text to Int 
            proxyPort = binding.proxyPort.text.toString().toInt()

            proxyUsername = binding.proxyUsername.text.toString()
            proxyPassword = binding.proxyPassword.text.toString()

            val combinedString = "Host: $proxyHost, Port: $proxyPort, Username: $proxyUsername, Password: $proxyPassword, Authentication: $isAuthenticationChecked"

          snackString(combinedString)
        }

        // Set OnCheckedChangeListener for the MaterialCheckBox
        binding.proxyAuthentication.setOnCheckedChangeListener { _, isChecked ->
            isAuthenticationChecked = isChecked // Store checkbox state
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}