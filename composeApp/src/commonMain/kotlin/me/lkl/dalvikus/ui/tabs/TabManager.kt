package me.lkl.dalvikus.ui.tabs

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.lkl.dalvikus.tabs.TabElement

class TabManager(initialTabs: List<TabElement>) {
    private val _tabs = mutableStateListOf<TabElement>().apply { addAll(initialTabs) }
    val tabs: List<TabElement> get() = _tabs
    val currentTab: TabElement
        get() = _tabs.getOrNull(selectedTabIndex) ?: _tabs.firstOrNull()
        ?: throw IllegalStateException("No tabs available")

    var selectedTabIndex by mutableStateOf(0)

    fun selectTab(index: Int) {
        if (index in _tabs.indices) {
            selectedTabIndex = index
        }
    }

    fun closeTab(tab: TabElement) {
        val index = _tabs.indexOf(tab)
        if (index != -1 && _tabs.size > 1) {
            _tabs.removeAt(index)

            if (selectedTabIndex >= _tabs.size) {
                selectedTabIndex = maxOf(0, _tabs.lastIndex)
            }
        }
    }

    fun addTab(tab: TabElement) {
        _tabs.add(tab)
    }

    fun addOrSelectTab(tab: TabElement) {
        val existingIndex = _tabs.indexOfFirst { it.tabId == tab.tabId }
        if (existingIndex != -1) {
            selectTab(existingIndex)
        } else {
            addTab(tab)
        }
    }
}
