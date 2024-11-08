package ani.dantotsu.parsers

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import ani.dantotsu.BottomSheetDialogFragment
import ani.dantotsu.databinding.BottomSheetExtensionTestSettingsBinding
import ani.dantotsu.parsers.novel.NovelExtensionManager
import com.xwray.groupie.GroupieAdapter
import eu.kanade.tachiyomi.extension.anime.AnimeExtensionManager
import eu.kanade.tachiyomi.extension.manga.MangaExtensionManager
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class ExtensionTestSettingsBottomDialog : BottomSheetDialogFragment() {
    private var _binding: BottomSheetExtensionTestSettingsBinding? = null
    private val binding get() = _binding!!
    private val adapter: GroupieAdapter = GroupieAdapter()
    private val animeExtension: AnimeExtensionManager = Injekt.get()
    private val mangaExtensions: MangaExtensionManager = Injekt.get()
    private val novelExtensions: NovelExtensionManager = Injekt.get()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetExtensionTestSettingsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.extensionSelectionRecyclerView.adapter = adapter
        binding.extensionSelectionRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.searchViewText.setText(searchQuery)
        binding.searchViewText.addTextChangedListener {
            searchQuery = it.toString()
        }
        
        binding.extensionTypeToggleGroup.check(
            when (extensionType) {
                "anime" -> binding.animeToggleButton.id
                "manga" -> binding.mangaToggleButton.id
                "novel" -> binding.novelsToggleButton.id
                else -> binding.animeToggleButton.id
            }
        )
        
        binding.testTypeToggleGroup.check(
            when (testType) {
                "ping" -> binding.pingToggleButton.id
                "basic" -> binding.basicToggleButton.id
                "full" -> binding.fullToggleButton.id
                else -> binding.pingToggleButton.id
            }
        )
        
        binding.extensionTypeToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.animeToggleButton.id -> {
                        extensionType = "anime"
                        extensionsToTest.clear()
                        setupAdapter()
                    }
                    binding.mangaToggleButton.id -> {
                        extensionType = "manga"
                        extensionsToTest.clear()
                        setupAdapter()
                    }
                    binding.novelsToggleButton.id -> {
                        extensionType = "novel"
                        extensionsToTest.clear()
                        setupAdapter()
                    }
                }
            } else {
                  if (group.checkedButtonId != checkedId) {
                      group.check(checkedId)
             }
          }
       }
        
        binding.testTypeToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    binding.pingToggleButton.id -> testType = "ping"
                    binding.basicToggleButton.id -> testType = "basic"
                    binding.fullToggleButton.id -> testType = "full"
                }
            } else {
                  if (group.checkedButtonId != checkedId) {
                      group.check(checkedId)
        }
     }
  }
            binding.extensionTypeTextView.setOnLongClickListener {
            binding.searchTextView.visibility = View.VISIBLE
            binding.searchView.visibility = View.VISIBLE
            true
        }
        setupAdapter()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupAdapter() {
        val namesAndUrls: Map<String,Drawable?> = when (extensionType) {
            "anime" -> animeExtension.installedExtensionsFlow.value.associate { it.name to it.icon }
            "manga" -> mangaExtensions.installedExtensionsFlow.value.associate { it.name to it.icon }
            "novel" -> novelExtensions.installedExtensionsFlow.value.associate { it.name to it.icon }
            else -> emptyMap()
        }
        adapter.clear()
        namesAndUrls.forEach { (name, icon) ->
            val isSelected = extensionsToTest.contains(name)
            adapter.add(ExtensionSelectItem(name, icon, isSelected, ::selectedCallback))
        }
    }

    private fun selectedCallback(name: String, isSelected: Boolean) {
        if (isSelected) {
            extensionsToTest.add(name)
        } else {
            extensionsToTest.remove(name)
        }
    }

    companion object {
        fun newInstance(): ExtensionTestSettingsBottomDialog {
            return ExtensionTestSettingsBottomDialog()
        }

        var extensionType = "anime"
        var testType = "basic"
        var searchQuery = "Chainsaw Man"
        var extensionsToTest: MutableList<String> = mutableListOf()
    }
}