package ru.kamaz.music.ui.producers

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music.ui.producers.ItemType.RV_ITEM_MUSIC_FOLDER
import ru.kamaz.music.view_models.FolderItemViewModel
import ru.kamaz.music_api.models.AllFolderWithMusic

import ru.sir.presentation.base.recycler_view.ViewHolderProducer


class MusicFoldersViewHolder: ViewHolderProducer<AllFolderWithMusic, FolderItemViewModel, TestTextItemBinding>(
    RV_ITEM_MUSIC_FOLDER, AllFolderWithMusic::class.java, FolderItemViewModel::class.java){
    override fun initBinding(inflater: LayoutInflater, parent: ViewGroup) =
        TestTextItemBinding.inflate(inflater,parent,false)

}