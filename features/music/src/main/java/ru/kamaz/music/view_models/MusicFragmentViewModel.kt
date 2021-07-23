package ru.kamaz.music.view_models

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import ru.biozzlab.twmanager.domain.interfaces.MusicManagerListener
import ru.biozzlab.twmanager.managers.MusicManager
import ru.biozzlab.twmanager.utils.easyLog
import ru.kamaz.music.data.MediaManager
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.services.MusicServiceInterface
import ru.kamaz.music_api.interactor.GetMusicPosition
import ru.kamaz.music_api.interfaces.Repository
import ru.kamaz.music_api.models.Track
import ru.sir.core.Either
import ru.sir.presentation.base.BaseViewModel
import javax.inject.Inject

class MusicFragmentViewModel @Inject constructor(
    application: Application,
    private val mediaPlayer: MediaPlayer,
    private val mediaManager: MediaManager,
    private val getMusicPosition: GetMusicPosition
) : BaseViewModel(application), MediaPlayer.OnCompletionListener, ServiceConnection,
    MusicServiceInterface.ViewModel,MusicManagerListener {
    private var tracks = ArrayList<Track>()
    private var currentTrackPosition = 0

    private val _isPlaying = MutableStateFlow(mediaPlayer.isPlaying)
    val isPlaying = _isPlaying.asStateFlow()
    private val _btModeActivation = MutableStateFlow(false)
    val btModeActivation = _btModeActivation.asStateFlow()

    val artist: StateFlow<String> by lazy {
      service.value?.getArtistName() ?: MutableStateFlow("Unknown")
    }

    val title: StateFlow<String> by lazy {
        service.value?.getMusicName() ?: MutableStateFlow("Unknown")
    }

    val isNotConnected: StateFlow<Boolean> by lazy {
        service.value?.checkDeviceConnection() ?: MutableStateFlow(true)
    }

    val test: StateFlow<Boolean> by lazy {
        service.value?.checkBTConnection() ?: MutableStateFlow(true)
    }
    val isNotConnectedUsb: StateFlow<Boolean> by lazy {
        service.value?.checkUSBConnection() ?: MutableStateFlow(true)
    }

    val isBtModeOn: StateFlow<Boolean> by lazy {
        service.value?.btModeOn() ?: MutableStateFlow(true)
    }

    private val _duration = MutableStateFlow("--:--")
    val duration = _duration.asStateFlow()

    private val _cover = MutableStateFlow("")
    val cover = _cover.asStateFlow()

    private val _mode = MutableStateFlow("")
    val mode = _mode.asStateFlow()


    private val _service = MutableStateFlow<MusicServiceInterface.Service?>(null)
    val service = _service.asStateFlow()

    var musicPosition: StateFlow<Int> =
        getMusicPosition().stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _maxSeek = MutableStateFlow(0)
    val maxSeek = _maxSeek.asStateFlow()

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }

    override fun init() {
        val audioAttributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        mediaPlayer.setOnCompletionListener(this)
        mediaPlayer.setAudioAttributes(audioAttributes)

        val intent = Intent(context, MusicService::class.java)
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate() {
        updateTracks(mediaManager)
        super.onCreate()
    }

    fun startTrack() {
        if (tracks.isEmpty()) {
            musicEmpty()
        } else
            service.value?.testPlay(tracks[currentTrackPosition])
    }

    fun firstOpenTrackFound(){
        if (tracks.isEmpty()) {
            musicEmpty()
        } else
            Log.i("222", "$tracks[currentTrackPosition] ")

    }
    fun playOrPause() {
        _isPlaying.value = service.value?.playOrPause() ?: false
    }


    fun checkPosition(position: Int) {
        service.value?.checkPosition(position)
    }

    fun previousTrack() {
        service.value?.previousTrack()
    }

    fun nextTrack() {
        service.value?.nextTrack()
    }

    fun updateTracks(mediaManager: MediaManager) {
        val result = mediaManager.scanTracks()
        if (result is Either.Right) {
            tracks.addAll(result.r)
        }
    }

    fun vmSourceSelection(action: MusicService.SourceEnum) {
        when (action) {
            MusicService.SourceEnum.BT -> {
                service.value?.sourceSelection(action)
                Log.d("test", "vmSourceSelection")
            }

            MusicService.SourceEnum.DISK -> {
                service.value?.sourceSelection(action)
            }

        }

    }

    fun musicEmpty() {
        Toast.makeText(context, "нет песен", Toast.LENGTH_LONG).show()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextTrack()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        Log.d("serWStart", "onServiceConnected: TRACK-VM")
        _service.value = (service as MusicService.MyBinder).getService()
        this.service.value?.setViewModel(this)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        _service.value = null
    }

    override fun addListener() {
        TODO("Not yet implemented")
    }

    override fun removeListener() {
        TODO("Not yet implemented")
    }

    override fun onCheckPosition(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onUpdateSeekBar(duration: Int) {
        _maxSeek.value = duration
    }


    override fun onSdStatusChanged(path: String, isAdded: Boolean) {
        "MicroSD status changed: value = $path status = $isAdded".easyLog(this)
    }

    override fun onUsbStatusChanged(path: String, isAdded: Boolean) {
        "USB status changed: value = $path status = $isAdded".easyLog(this)
    }

    override fun selectBtMode() {
        Log.d("test", "${_btModeActivation.value} hui")
        Log.d("test", "${_btModeActivation.value} hui")
    }


}