package ru.kamaz.music_api.models

import java.io.Serializable


data class FavoriteSongs(val idSong: Int,
                         val data:String
                        ) : Serializable

