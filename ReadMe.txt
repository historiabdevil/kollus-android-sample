#####################
#                   #
#     수정 사항     #
#                   #
#####################

2020.01.18
1. ToolChain 및 빌드 스크립트 변경
2. CpuInfo에 isSupportDevice함수 추가
3. 외부에서 플레이어 ID 설정하는 경우 중복차단 안되는 현상 수정
4. OMXCodec에서 연속된 Seek시 죽는 현상 수정
5. Seek시 다음 구간의 자막까지 안나오는 현상 수정
6. onPrepared 이후 자막 올리도록 수정
----------------------------------------------------------------------------------------
2019.12.10
1. LMS 데이터에 치환자 TIMEMAP_DATA 추가
2. 에뮬레이터 체크 로직 변경
----------------------------------------------------------------------------------------
2019.11.13
1. SW 코덱시 간헐적으로 죽는 현상 수정
2. DRM Callback/Play Callback 동적 인자 추가로 아래 항목 수정
	. KollusStorage
		기존 : public int load(String path, KollusContent content);
		수정 : public int load(String path, String extraDrmParam, KollusContent content);
	. MediaPlayer
		기존
			public void setDataSourceByUrl(String path);
			public void setDataSourceByKey(String mediaContentKey);
		수정
			public void setDataSourceByUrl(String path, String extraDrmParam);
			public void setDataSourceByKey(String mediaContentKey, String extraDrmParam);
3. 북마크 추가 시 라벨도 입력 받을 수 있도록 수정 
	 MediaPlayer
		기존 : public void updateKollusBookmark(int position);
		수정 : public void updateKollusBookmark(int position, String label);
4. Mpeg Dash ABR 추가
		
----------------------------------------------------------------------------------------
2019.10.08_r1
1. 라이브(HLS, ABR) 추가
2. 라이브 LMS 추가
3. Android 10에서 죽는 문제 수정
4. SW Codec 싱크 틀어지는 현상 수정
	2019.08.22_r1의 버그
----------------------------------------------------------------------------------------
2019.08.22_r1
1. uservalue 10개 제한 제거
2. 재생 종료 후 곧바로 재생시 간헐적으로 -8661오류나는 현상 수정
3. disable nscreen 스펙 추가
	true이면 재생 종료시 nscreen 데이터를 전송하지 않음
4. x86 64비트 라이브러리 추가
5. 배속 재생 시 자막 싱크 틀어지는 현상 수정
6. interface OnInfoListener의 콜백 함수 변경
	void onBandwidth(MediaPlayer mp, int bandwidth);에서 void onBandwidth(MediaPlayer mp, BandwidthItem item);로 변경
	void onDetectBandwidth(MediaPlayer mp, List<BandwidthItem> list); 추가
7. 재생 중에 외부 앱에서 스카마로 재생 호출 시 행걸리는 현상 수정
----------------------------------------------------------------------------------------
2019.05.23_r1
1. Emulator 체크를 자동으로 할 수 있도록 수정
	MediaPlayer에 API 추가
		public void setEmulatorCheckerListener(EmulatorCheckerListener listener);
	public interface EmulatorCheckerListener {
    		public void onRunningEmulator();
	}
2. DRM Callback, PlayCallback에 단말시간도 같이 올림
3. ClientUserID가 이메일 형식이면 북마크를 못가져오는 현상 수정

----------------------------------------------------------------------------------------

2019.04.09_r1
1. 중복 재생 차단 체크 시 시간정보도 보도록 수정

----------------------------------------------------------------------------------------

2019.03.21_r1
1. 갤럭시 와이드3(SM-J737)에서 재생 시도 시 죽는 현상 수정
2. 일부 긴 MP3 컨텐츠의 Seek가 오래 걸리는 현상 수정
3. DRM Callback의 kind3에 expiration_date 항목 추가
4. 소프트웨어 코덱일 경우 고배속 재생 중에 죽는 현상 수정
5. 일부 단말(SM-T280)에서 소리 크게 재생되는 현상 수정
	2019.02.15_r1부터 발생

----------------------------------------------------------------------------------------

2019-02-22
1. 인트로 재생중에 다음 영상으로 넘어갈 때 죽는 문제 수정

----------------------------------------------------------------------------------------

2019-02-15
1. Stop중 Play시 행걸림 현상 수정
2. 백그라운드 재생 중 Seek 안되는 현상 수정

----------------------------------------------------------------------------------------

2019-01-08

1. 화면 끊김 현상 수정
	블루투스 연결 시 100% 재현
2. EmulatorDetector 패키지 변경
	com.kollus.sdk.media.util --> com.kollus.sdk.media.util.emulatordetector
3. EmulatorDetectorViaSensor 추가 (테스트 안됨)
	AccelerometerDetector accelerometerDetector = AccelerometerDetector.builder()
	        .setDelay(500)
	        .setEventCount(5)
	        //check continues 500*5 = 2500ms
	        .build();

	GyroscopeDetector gyroscopeDetector = GyroscopeDetector.builder()
	        .setDelay(500)
	        .setEventCount(5)
	        .build();
	EmulatorDetectorViaSensor emulatorDetector = new EmulatorDetectorViaSensor(accelerometerDetector, gyroscopeDetector);
	emulatorDetector.detect(mContext, new Callback() {
	    @Override
	    public void onDetect(boolean isEmulator) {
	        if(isEmulator) {
	            //TODO
	            //비디오 Pause
	            //경고창
	        }
	    }

	    @Override
	    public void onError(Exception exception) {
	        Log.e(TAG, exception.getMessage());
	    }
	});
4. 비디오 워터 마크 정보 KollusContent에 추가
	/**
	 * SDK 내부에서 사용
	 * @param code 비디오 워터마크 코드 설정
	 */
	public void setVideoWaterMarkCode(String code);

	/**
	 * 비디오 워터마크 코드 가져오는 함수
	 */
	public String getVideoWaterMarkCode();

	/**
	 * SDK 내부에서 사용
	 * @param alpha 비디오 워터마크 투명도 설정
	 */
	public void setVideoWaterMarkAlpha(int alpha);

	/**
	 * 비디오 워터마크 투명도 가져오는 함수
	 */
	public int getVideoWaterMarkAlpha();

	/**
	 * SDK 내부에서 사용
	 * @param size 비디오 워터마크 크기 설정
	 */
	public void setVideoWaterMarkFontSize(int size);

	/**
	 * 비디오 워터마크 크기 가져오는 함수
	 */
	public int getVideoWaterMarkFontSize();

	/**
	 * SDK 내부에서 사용
	 * @param color 비디오 워터마크 색상 설정
	 */
	public void setVideoWaterMarkFontColor(int color);

	/**
	 * 비디오 워터마크 색상 가져오는 함수
	 */
	public int getVideoWaterMarkFontColor();

	/**
	 * SDK 내부에서 사용
	 * @param sec 비디오 워터마크 보여주는 시간(sec) 설정
	 */
	public void setVideoWaterMarkShowTime(int sec);

	/**
	 * 비디오 워터마크 보여주는 시간(sec) 가져오는 함수
	 */
	public int getVideoWaterMarkShowTime();

	/**
	 * SDK 내부에서 사용
	 * @param sec 비디오 워터마크 숨기는 시간(sec) 설정
	 */
	public void setVideoWaterMarkHideTime(int sec);

	/**
	 * 비디오 워터마크 숨기는 시간(sec) 가져오는 함수
	 */
	public int getVideoWaterMarkHideTime();

	/**
	 * SDK 내부에서 사용
	 * @param space 비디오 워터마크 공간 분할 수 설정
	 */
	public void setVideoWaterMarkSpace(int space);

	/**
	 * 비디오 워터마크 공간 분할 수 가져오는 함수
	 */
	public int getVideoWaterMarkSpace();

	/**
	 * SDK 내부에서 사용
	 * @param random 비디오 워터마크 렌덤 표시 설정
	 */
	public void setVideoWaterMarkRandom(boolean random);

	/**
	 * 비디오 워터마크 표시가 렌덤인지 여부
	 */
	public boolean isVideoWaterMarkRandom();
5. kind3에서 expire date를 조정하여 만료된 경우 처음 에러값이 예전것이 올라가는 오류 수정
6. 버전 정보
	* AAR : 2019.01.08_r1

----------------------------------------------------------------------------------------

2018-12-11

1. Seek중 종료 시 LMS 데이터 튀는 현상 수정
2. DB의 LMS 데이터가 삭제 안되는 현상 수정
3. 자막 끝에 개행이 들어가는 현상 수정
4. 구간 재생 시 전체 반복 안되는 현상 수정
5. 버전 정보
	* AAR : 2018.12.05_r1

----------------------------------------------------------------------------------------

2018-11-xx

1. HLS 타임머신 지원
2. 버전 정보
	* AAR : 2018.11.22_r1

----------------------------------------------------------------------------------------
2018-08-24

1. 전체 반복시 소리 안나오는 현상 수정.
	사운드코덱의 상태가 EOS가 된 상태에서 Seek시 상태를 리셋 안해서 발생
2. 버전 정보
	* AAR : 2018.08.24_r1

----------------------------------------------------------------------------------------
2018-08-16

1. 오디오 파일의 경우 이어보기 안되는 현상 수정
	ABR(HLS, DASH)를 위해 사운드 디코드 버퍼를 두고 있는데
	seek된 후의 데이터를 줘야 하는데 버퍼의 데이터를 줘서 발생.
2. 버전 정보
	* AAR : 2018.08.16_r1

----------------------------------------------------------------------------------------
2018-08-14

1. 네트워크 요청 시 no-cache 헤더 옵션 추가
2. 구간재생(Preview) 안되는 현상 수정
3. 다중 컨텐츠(인트로 포함된 영상) 재생 시 구간재생(Preview)이 있는 경우 다음 영상 안 넘어가는 현상 수정
4. 버전 정보
	* AAR : 2018.08.14_r1

----------------------------------------------------------------------------------------

2018-08-02

1. Sound Decode 연속 실패 시 죽는 현상 수정
2. 버전 정보
	* AAR : 2018.08.02_r1

----------------------------------------------------------------------------------------

2018-07-31

1. 2018.07.27_r1의 로그 추가 버전
	시작 시에 -1011오류 현상 확인을 위한 로그 추가
2. 버전 정보
	* AAR : 2018.07.31_r1

----------------------------------------------------------------------------------------

2018-07-26

1. 워터마크 적용된 컨텐츠 재생 오류 나는 현상 수정
2. 버전 정보
	* AAR : 2018.07.27_r1

----------------------------------------------------------------------------------------

2018-07-26

1. 2018.07.23_r1에서 죽는 현상이 발견되어 수정
2. 버전 정보
	* AAR : 2018.07.26_r1

----------------------------------------------------------------------------------------

2018-07-25

1. 에러 문구 변경
2. 버전 정보
	* AAR : 2018.07.23_r1

----------------------------------------------------------------------------------------

2018-07-20

1. 2018.07.18_r1에서 죽는 현상 수정
	2018.07.18_r1의 수정사항은 그대로 유지하고 해당 SDK는 폐기
2. 버전 정보
	* AAR : 2018.07.20_r1

----------------------------------------------------------------------------------------

2018-07-19

1. 일부 롤리팝이상의 단말에서 HW Codec 연동 안되는 문제 수정
2. SW Codec 연동 시 B-Frame이 많이 들어 있는 컨텐츠의 경우 Frame Rate가 떨어지는 현상 수정
3. LMS데이터의 {JSON_DATA} 태그 적용 시 content_info블럭에 showtime이 빠져있는 현상 수정
4. OnInfoListener에 두개의 API 추가
	void onDownloadRate(MediaPlayer mp, int downloadRate);
        void onBandwidth(MediaPlayer mp, int bandwidth);
5. 버전 정보
	* AAR : 2018.07.18_r1

----------------------------------------------------------------------------------------

2018-06-28

1. drm callback kind 3의 result가 0이면 -8655가 아닌 -8657이 리턴되야 함.
2. 재생 에러시 에러코드가 -1105로 overwrite되는 현상 수정
3. 버전 정보
	* AAR : 2018.06.28_r1

----------------------------------------------------------------------------------------

2018-06-26

1. 특정 단말에서 재생 중 버퍼링으로 빠지는 현상 수정(2018.03.28_r1 이후는 패치 요망)
2. 버전 정보
	* AAR : 2018.06.26_r1

----------------------------------------------------------------------------------------

2018-06-14

1. 일부 단말에서 화면 회전 시 SW 코덱에서 화면 깨짐 수정
	SM-P605L(노트 10.1 2014 에디션, 4.4.2)
	LG-F350K(G Pro2, 4.4.2)
2. 미디어 파일 가져오다 -8302 에러나는 현상 수정
3. 버전 정보
	* AAR : 2018.06.11_r1

----------------------------------------------------------------------------------------

2018-06-04

1. Audio WaterMark 컨텐츠인 경우 죽는 현상 수정
2. 버전 정보
	* AAR : 2018.06.04_r1

----------------------------------------------------------------------------------------

2018-05-30

1. 북마크 DB 클래스를 생성하지 않아서 죽는 현상 수정
2. 버전 정보
	* AAR : 2018.05.30_r1

----------------------------------------------------------------------------------------

2018-05-24

1. 종료 시 nscreen, lms 데이터를 기본으로 async하게 보냄
2. offline bookmark 지원
3. hls 지원
4. 버전 정보
	* AAR : 2018.05.24_r1

----------------------------------------------------------------------------------------

2018-04-19

1. 화면 번짐 수정
	* Galaxy S3 및 다수의 갤럭시 모델
2. 해당 시간의 자막 내용이 없는(공백) 경우 죽는 현상 수정

3. 버전 정보
	* AAR : 2018.04.18_r1

----------------------------------------------------------------------------------------

2018-04-12

1. 홈보이 HW코덱 연동 시 죽는 현상 수정
	* 죽는 부분에 try-catch하여 자동으로 SW코덱으로 연동되도록 수정
2. 버전 정보
	* AAR : 2018.04.12_r1

----------------------------------------------------------------------------------------

2018-04-09

1. 화면 번짐 수정
	* ETOOSPAD5, ETOOSPAD6, KA-E410W(교원 아이나비)
2. MediaPlayer 생성자 변경
	* public MediaPlayer(Context context, KollusStorage storage, int port);
3. 플레이어 종료 시 크래시 나는 현상 수정
4. 롤리팝이상에서 하드웨어 코덱 사용 시 비디오 렌더링이 부드럽지 않는 현상 수정
	* 코덱의 timestamp정보 오류
5. KollusStorage에 KollusPlayerCallbackListener 추가
	* 스트리밍 재생 시 PlayerCallback에서 주고 받은 데이터를 확인할 수 있는 리스너
6. KollusStorage에 isDownloadedContent 제거
	* 다운로드 완료된 컨텐츠를 다운로드할 경우 complete를 notify함
7. 버전 정보
	* AAR : 2018.04.09_r1

----------------------------------------------------------------------------------------

2018-03-28

1. 소프트웨어 볼륨 추가
	* MediaPlayer.setVolume
	* 15단계를 기준으로 30% 증폭할 수 있습니다.
2. EDUDIC7(EDP-E70), DICPLE(IDT700)의 HW 코덱에서 화면 번짐 수정
3. Utils에서 아래와 같은 렌더링 관련 API 추가
	* public static void setRenderType(Context context, MediaPlayerBase.RENDER_MODE mode)
	* public static MediaPlayerBase.RENDER_MODE getRenderType(Context context);
	* public static MediaPlayerBase.RENDER_MODE getRenderModeByInt(int mode);
4. 버전 정보
	* AAR : 2018.03.28_r1

----------------------------------------------------------------------------------------

2018-03-22

1. PlayCallback, DrmCallback에 expiration_playtime_type 추가
	* 0이면 expiration_playtime의 값은 기존대로 영상 시간 기준
		예로 30이면 배속에 상관없이 영상을 총 30초 분량 볼 수 있음
	* 1이면 expiration_playtime의 값은 영상 재생하는데 사용한 시간 기준
		예로 30이면 2배속으로 볼 경우 영상을 총 60초 분량 볼 수 있음
2. MediaPlayer에 AV 싱크 조절 함수 추가
	* public void setAudioDelay(int timeMs);
	* 소리가 느린 경우 양수값을 빠른 경우 음수값을 주면 됨
3. -2147483648 오류 코드
	* 해당 오류는 다른 오류코드가 덮어져서 발생하는 코드로 원래 오류 코드가 나올 수 있도록 수정
4. 재생 또는 다운로드 시작 시 -8056 날 수 있는 현상 수정

5. 버전 정보
	* AAR : 2018.03.20_r1

----------------------------------------------------------------------------------------
2018-02-12

1. 썸네일 다운로드 관련 스펙 추가
	- 썸네일 다운로드를 Thread 방식으로 받을 경우 관련된 인터페이스
	public interface KollusPlayerThumbnailListener {
		void onCached(int index, int nErrorCode, String savePath);
	}

	- KollusStorage 추가된 API
	/**
	 * KollusPlayerThumbnailListener 등록 함수
	 * @param listener
	 */
	public void setKollusPlayerThumbnailListener(KollusPlayerThumbnailListener listener);

	- KollusContent 추가된 API
	/**
	 * SDK 내부에서 사용
	 * @param enable 썸네일 활성화 여부 설정
	 */
	public void setThumbnailEnable(boolean enable);

	/**
	 * 썸네일 활성화 여부
	 * @return
	 */
	public boolean isThumbnailEnable();

	/**
	 * SDK 내부에서 사용
	 * @param bThread 썸네일 다운로드 쓰레드 방식 여부 설정
	 */
	public void setThumbnailThread(boolean bThread);

	/**
	 * load 시 썸네일 다운로드가 Sync(no thread)인지 여부
	 * @return
	 */
	public boolean isThumbnailDownloadSync();

2. 버전 정보
	* AAR : 2018.02.13_r1

----------------------------------------------------------------------------------------

2018-01-24

1. weak reference 제거
	* notify하다가 죽는 이슈가 보고되었는데 GC에서 weak reference를 회수하여 발생한 것으로 의심이 됨
2. BroadCastReceiver를 unregister하다가 죽는 이슈가 보고되어 try~catch함
	* MediaPlayer의 release를 여러번 호출해서 발생했을 것으로 의심이 됨
3. 재생 시작과 동시에 종료 시 죽는 현상 수정
	* 자바단에서 미디어 정보를 확인 후에 native단으로 넘기는데 정보 확인 중에 종료가 되어 발생한 현상입니다.
	  미디어 정보를 native단에서 확인하도록 수정하였습니다.

4. 버전 정보
	* AAR : 2018.01.24_r1

----------------------------------------------------------------------------------------

2018-01-09

1. 중국어 간체 에러 메세지 추가
2. 중복 차단 URL이 https인 경우 안되는 현상 수정
3. 버전 정보
	* AAR : 2018.01.08_r1

----------------------------------------------------------------------------------------

2017-12-08

1. LMS 데이터에 치환자 {DEVICE}, {REAL_PLAYTIME} 추가
2. 버전 정보
	* AAR : 2017.12.08_r1

----------------------------------------------------------------------------------------

2017-12-06

1. pause상태에서 seek시 current position이 seek전의 값을 주는 버그 수정
	seek complete가 오기 전에 종료 시에 이어보기 정보가 0으로 올라가는 버그가 있어서
	seek 전의 값이 올라가도록 수정하였는데 그 side effect 현상이었습니다. 
	seek하려고 하는 값이 올라가도록 수정하였습니다.
2. IRIS tab 2에서 죽는 현상 수정
	X86단말로 feature에 neon이 있어서 arm neon으로 라이브러리를 로드해서 발생했습니다.
3. 버전 정보
	* AAR : 2017.12.06_r1

----------------------------------------------------------------------------------------

2017-12-05(폐기)

1. seek complete 오기전에 seek 가능하도록 수정
2. 버전 정보
	* AAR : 2017.12.05_r1

----------------------------------------------------------------------------------------

2017-11-30

1. 약전계에서 seek후 곧바로 종료 시 ANR 현상 수정
2. 종료 시 보내는 nscreen, lms 정보를 UI로 응답 받을지 여부 설정하는 함수 추가
	* 기본은 On이며, Off이면 별도의 쓰레드로 해당 데이터를 처리하므로 종료가 조금 더 빠릅니다.
	* public void setNotifyLastReport(boolean bOn);
3. LMS에 {PLAY_STATUS}, {RUN_TIME} 치환자 추가
	* 플레이어 상태 : play, pause, stop
4. 버전 정보
	* AAR : 2017.11.30_r1

----------------------------------------------------------------------------------------

2017-11-01
1. 구글 스토어의 보안 정책에 의한 변경
	1. so를 다운로드 방식에서 앱에 탑재하는 방식으로 변경
	2. 플레이어 생성 interface 변경
		1. MediaPlayerFactory 제거
		2. MediaPlayer 추가
		3. MediaPlayerBase의 아래의 interface가 MediaPlayer로 이동
			1. OnPreparedListener
			2. OnCompletionListener
			3. OnErrorListener
			4. OnInfoListener
			5. OnBufferingUpdateListener
			6. OnSeekCompleteListener
			7. OnVideoSizeChangedListener
			8. OnTimedTextDetectListener
			9. OnTimedTextListener
			10. OnExternalDisplayDetectListener
2. 오류 수정
	1. 재생 중에 전화가 오면 LMS 데이터가 초기화되는 현상 수정
	2. 성우모바일 sm-508 단말에서 SW 코덱 화면 번짐 수정
		1. HW 코덱은 여전히 지원 안됨
	3. onPrepared 받은 후 start전에 배속 조정이 안되는 현상 수정
3. 버전 정보
	* AAR : 2017.11.01_r1

----------------------------------------------------------------------------------------

2017-09-28

1. DRM Callback V1에서 다운로드 후 재생 시 -8687 오류 나는 현상 수정


Version
	AAR : 2017.09.28_r1

----------------------------------------------------------------------------------------
2017-09-18

1. release시 sdk에서 app으로 메세지 전달 중에 crash 수정
2. 네트워크가 좋은 상태에서도 오류나는 현상 수정
	북마크, 이어보기, 진도률등이 영향을 받았습니다.


Version
	AAR : 2017.09.18_r1

----------------------------------------------------------------------------------------

2017-09-07

1. Utils.getExternalMounts 제거
2. 아래의 API가 Utils클래스에 추가
    public static void setDecoderType(Context context, DecoderType type);
    public static DecoderType getDecoderType(Context context);


Version
	AAR : 2017.09.07_r1

----------------------------------------------------------------------------------------

2017-09-04

1. SW Codec 싱크 문제 수정
2. 갤럭시 S6에서 재생 중에 Hang 걸리는 현상 수정
3. LMS Callback 데이터에 플레이어 실제 구동시간 추가
4. DRM Callback 데이터에 만료날짜 외에 체크 날짜 추가
5. KollusStorage.setCacheSize를 0으로 주면 스트리밍 재생 후 자동 캐시 삭제
6. DRM Callback kind2/kind3에 check_expiration_date 추가
	expiration_date(A)를 길게 잡고 check_expiration_date(B)를 짧게 잡으면 
	컨텐츠 정보에는 만료기한이 A로 나오지만 
	B기한내에 kind3로 DRM 갱신을 하지 않으면 컨텐츠를 강제만료 시키는데 사용
7. DRM Callback kind2 응답이 result가 1이 아니거나 네트워크로 응답을 받지 못하는 경우 강제 만료 처리

Version
	AAR : 2017.09.04_r1

----------------------------------------------------------------------------------------

2017-07-24

1. Eduple W 화면 번짐 수정

Version
	AAR : 2017.07.18_r1

----------------------------------------------------------------------------------------

2017-06-20

1. KollusMediaPlayer의 1670 라인 NullPointerException 오류 수정
	캡춰툴을 감지하기 위해 실행되고 있는 앱을 검사하는데
	실행되고 있는 앱이 없는 경우에 발생
2. DRM Callback kind3의 일괄갱신과 재생을 위해 갱신 분리
	reset_req가 1이면 일괄갱신이고 0이면 재생을 위한 호출임

Version
	AAR : 2017.06.20_r1

----------------------------------------------------------------------------------------

2017-04-26

1. ICS미만에서 죽는 현상 수정

Version
	AAR : 2017.04.26_r1

----------------------------------------------------------------------------------------

2017-02-21

1. setPlayingRate 인자 double에서 float로 변경

2. KollusContent의 배속 Disable관련 API 추가
	public void setDisablePlayRate(boolean disable);
	public boolean getDisablePlayRate();

3. KollusContent의 Seekable End관련 API 추가
	public void setSeekableEnd(int end);
	public int getSeekableEnd();

4. 플레이어 생성자 프록시 포트 번호 인자 추가
	public MediaPlayerFactory(int port, OnCreateListener listener);

5. Duration을 미디어 메타정보에서 가져오지 않고 오디오, 비디오 트랙 정보에서 가져오는 것으로 수정

Version
	AAR : 2017.02.02_r1

----------------------------------------------------------------------------------------

2017-01-16

1. DRM 일괄 갱신 리스너 변경
    /**
     * DRM 갱신 시작을 알려주는 함수
     */
    void onDRMUpdateStart();

    /**
     * 현재 DRM정보 갱신 진행상황을 알려주는 함수
     * @param request 서버에 전송한 데이터
     * @param response 서버에 전송 받은 데이터
     */
    void onDRMUpdateProcess(String request, String response);

    /**
     * DRM 갱신이 완료됐을 때 알려주는 함수
     */
    void onDRMUpdateComplete();


Version
	AAR : 2017.01.13_r1

----------------------------------------------------------------------------------------

2017-01-12

1. DRM 일괄 갱신 시 콜백 데이터를 10건씩 묶어서 처리

2. DRM 콜백 오프라인 데이터를 10건씩 묶어서 처리

3. DRM callback kind 1, 3에 check_abuse 추가
   check_abuse가 1이면 kind 3을 무조건 태우게 됩니다. 즉 네트워크 안되는 상황에서
   플레이 안되게 됩니다.


Version
	AAR : 2016.12.29_r1

----------------------------------------------------------------------------------------

2016-12-02

1. seek를 자주 할 경우 LMS의 real_playtime이 비정상적으로 커지는 현상 수정


Version
	AAR : 2016.12.02_r1

----------------------------------------------------------------------------------------

2016-12-01

1. 다운로드 컨텐츠 LMS 전송을 ON/OFF하는 API 추가
	public abstract void setLmsOffDownloadContent(boolean bOff);

2. 북마크 전송 시 user value 추가

3. LMS 데이터에 real_playtime 추가



Version
	AAR : 2016.11.21_r1

----------------------------------------------------------------------------------------

2016-11-11

1. ICS 단말에서 시작하자마자 죽는 현상 수정
	curl관련 라이브러리(crypto, ssl, curl)를 static에서 shared로 바꾼 후 라이브러리간
	함수를 찾지 못해서 발생되었습니다. 하나의 shared 라이브러리로 바꾼 후 해결되었습니다.
	

Version
	AAR : 2016.11.09_r1

----------------------------------------------------------------------------------------

2016-11-03

1. 다운로드 컨텐츠가 많은 경우 리스트를 가져올 때 오래 걸리는 현상 수정

2. 캐시 설정 API 추가
	KollusStorage.setCacheSize

Version
	AAR : 2016.11.02_r1

----------------------------------------------------------------------------------------

2016-10-27

1. 삼성 단말에 죽는 현상 수정
	원인은 ffmpeg을 빌드한 컴파일러와 호환이 안되어 발생했습니다.
	ndk-r12b의 4.9에서 ndk-r8e의 4.4.3으로 빌드했습니다.

2. so 버전 관리를 SDK내부에서 합니다. 따라서 Library.Checker의 원형이 바뀌었습니다.
	public void check(boolean releaseMode);
	releaseMode가 true이면 로그가 최소화된 라이브러리를 체크하게 되고
	false이면 로그가 있는 라이브러리를 체크하게 됩니다.

3. KollusStorage의 public boolean isReady();가 추가 되었습니다.
	리턴값은 setDevice(setDeviceASync)의 호출이 성공적으로 되어서 사용할
	준비가 되었는 지 여부입니다.

Version
	AAR : 2016.10.25_r1

----------------------------------------------------------------------------------------

2016-09-26

1. Android 7.0(Nougat) 지원

Version
	Server : 1.4.3(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.8.4(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.8.2(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.12
	FFMpeg : 1.0
	AAR    : 2.19

----------------------------------------------------------------------------------------

2016-08-23

1. 동일한 MediaContentKey로 연속해서 인트로 구성 시 발생하는 재생 오류 수정

2. MediaPlayerFactory.createMediaPlayer에 화면 출력 방식 인자 추가
	public void createMediaPlayer(Context context, VideoWindowImpl impl, MediaPlayerBase.RENDER_MODE mode, boolean bAutoUpdate, KollusStorage storage, Uri path);
	SW Codec의 경우 화면 출력 시 버퍼 화면사이즈를 설정을 해야 하는데 
	기존에는 모델명 기준으로 하드 코딩하여 2승수, 2배수, 16배수로 설정하였습니다.
	따라서 단말 지원에 어려움이 발생하기에 아예 인자로 입력을 받아서 처리하는 방식으로 변경하였습니다.
	MediaPlayerBase.RENDER_MODE.RENDER_MODEL가 기존 방식으로 화면 검은 색으로 나오거나 죽을 경우
	RENDER_2_POWER, RENDER_2_MULTIPLE, RENDER_16_MULTIPLE 중 하나를 주어야 합니다.

	MediaPlayerBase.RENDER_MODE.RENDER_MODEL
	MediaPlayerBase.RENDER_MODE.RENDER_2_POWER
	MediaPlayerBase.RENDER_MODE.RENDER_2_MULTIPLE
	MediaPlayerBase.RENDER_MODE.RENDER_16_MULTIPLE

3. 처음 play시에는 lms 보내도록 수정

Version
	Server : 1.4.2(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.8.2(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.8.1(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.12
	FFMpeg : 1.0
	AAR    : 2.18

----------------------------------------------------------------------------------------

2016-08-03

1. 자막이 잠깐 나왔다가 사라지는 현상 수정

2. 엠피지오 Legend-Q에서 특정 영상 재생 안되는 현상 수정
   영상의 화면이 사이즈가 16배수가 아닌 영상은 재생시 화면이 찌그러지거나 죽는 현상이 있었습니다.

3. 인트로 영상이 나온 후, 본 영상 재생시 비주기로 -8615 오류나는 현상 수정

Version
	Server : 1.4.1(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.8.1(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.8.0(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.12
	FFMpeg : 1.0
	AAR    : 2.17

----------------------------------------------------------------------------------------

2016-07-12

1. 백그라운드에서 영상이 시작한 후 포그라운드로 올라오면 락거리는 현상 수정

2. MediaPlayerFactory로 플레이어 생성 중 UI 락거리는 현상 수정

Version
	Server : 1.4.0(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.8.0(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.8.0(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.11
	FFMpeg : 1.0
	AAR    : 2.16

----------------------------------------------------------------------------------------

2016-07-09

1. 인트로 재생 후 본영상 재생 시 처음부터 재생 후 이어보기 되는 오류 수정

2. 인트로 영상(5초)에서 Seek 시 오류나는 현상 수정

3. 인트로 영상에서 포그라운드로 재진입 시에 검은 화면만 나오는 현상 수정

4. 다운로드 중에 디스크 쓰기 오류(Disk Full)시 락걸림 현상 수정

5. cache, player, server OpenSSL 보안 패치

6. 플레이어 생성방식 변경

Version
	Server : 1.4.0(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.8.0(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.8.0(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.11
	FFMpeg : 1.0
	AAR    : 2.16

----------------------------------------------------------------------------------------

2016-06-14

1. Offline LMS 데이터 오류
   -. serial 오류 수정
   -. 누적되지 않는 오류 수정

Version
	Server : 1.3.12(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.22(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.10(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.9
	FFMpeg : 1.0
	AAR    : 2.15

----------------------------------------------------------------------------------------

2016-06-01

1. ICS미만 단말에 죽는 현상 수정

2. 오디오 컨텐츠의 경우 빠른 Seek시 죽는 문제 수정

3. 다운로드 컨텐츠를 재생 시 간헐적으로 -8464 오류나는 현상 수정

Version
	Server : 1.3.11(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.21(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.9(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.8
        FFMpeg : 1.0
	AAR    : 2.14

----------------------------------------------------------------------------------------

2016-05-19

1. 비디오게이트웨이 서버 오류 메세지 분리

2. SDK내에서 처리하던 화면꺼짐 관련 코드 제거

Version
	Server : 1.3.10(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.20(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.9(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.7
        FFMpeg : 1.0
	AAR    : 2.13

----------------------------------------------------------------------------------------

2016-04-26

1. DRM 일괄 갱신 API 변경
   public void updateDownloadDRMInfo(KollusPlayerDRMUpdateListener listener, boolean bAll);
   bAll --> true면 모든 컨텐츠 갱신, false면 만료된 컨텐츠만 갱신

Version
	Server : 1.3.9(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.19(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.8(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.6
        FFMpeg : 1.0
	AAR    : 2.12

----------------------------------------------------------------------------------------

2016-04-18

1. 멀티 코어 단말에서 SW 코덱 사용 시 AV 싱크 어긋나는 현상 수정
   FFMpeg에서 코어 수만큼 디코딩 pending 버퍼가 있어서 발생

2. DRM 일괄 갱신
   StorageManger에 아래 함수 추가
   public void updateDownloadDRMInfo(KollusPlayerDRMUpdateListener listener);

3. 인트로 추가

4. x86 64비트 단말(요가탭3 프로, 미패드 2) 지원

5. DRM Callback kind 3의 응답에 서버오류 발생 시 컨텐츠의 DRM 정책을 따르도록 수정

6. pause시 시간이 흐르는 현상은 재현이 안됨.
   알려주신 재현 경로인 pause, resume을 반복하는 것은 resume시 seek이 있어서 발생했습니다.
   seek시 코덱을 초기화하기 때문에 리소스를 많이 소모하는 작업으로 resume시 seek는 작업입니다.
   일전에 pause된 상태에서 앱이 백그라운드로 내려간 후 포그라운드로 올라왔을 때
   surface가 재생성되는 관계로 검은화면이 나오는 것을 방지하기 위해서 seek를 하라고 안내를 하였는데요.
   Activity의 onResume시에만 seek를 하셔야 합니다.

Version
	Server : 1.3.9(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.19(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.8(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.6
        FFMpeg : 1.0
	AAR    : 2.12

변경된 사항
	FFMpeg을 제외한 모든 라이브러리가 바뀌었습니다.

----------------------------------------------------------------------------------------

2016-02-03

1. 롤리팝이상의 단말에서 HD 2배속 재생 중 Seek 시 ANR 오류 수정

Version
	Server : 1.3.6(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.13(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.6(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.2
	AAR    : 2.9

변경된 사항
	AAR, 플레이어 버전
	참고로 이번 버전부터는 배속라이브러리를 Sonic으로 사용합니다.

----------------------------------------------------------------------------------------


2016-02-02

1. Note3에서 av sink 문제 수정
	HW Codec은 속도가 빠르다는 생각에 output queue를 rendering 시점에서 가져와서 발생했습니다.
	코덱에 input한 시점과 처음 output되는 시점의 차이만큼 sink가 차이가 발생했습니다.
	즉, 단기 플레이어는 이어보기시에 seekToExact를 위해
	decoded frame을 버리게 되는데 실제로는 output없기 때문에 버리지 못하고, 그 차이만큼 sink 벌어지게 되었습니다.
	Note3가 유난히 sink 차이가 심한 이유는 codec의 input queue 22개로 타단말에 비해 input queue개수가
	커서 눈으로 확연히 확인이 가능했던 것으로 보입니다.


2. Nexus7 1세대에서 소리 늘어지는 현상 수정
	사운드 코덱으로 HW Codec를 사용해서 발생했습니다. 기존대로 사운드는 SW Codec을 사용하도록 했습니다.

3. 배속 라이브러리 교체(player version 1.7.12)
	

4. MediaPlayerBase.setVideoRendering(boolean bRender) 추가
	앱이 백그라운드 또는 포그라운드 진입시 자동으로 호출됩니다.
	그이외 목적으로 사용하실 때 사용할 수 있도록 추가했습니다.

Version
	Server : 1.3.6(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.11(r/d) d:로그 버전 r:로그 없는 버전
			기존 배속 라이브러리 (SoundTouch)로 G3 마시멜로우에서 소리끊김 현상이 보고 되었습니다.
		 1.7.12(r/d) d:로그 버전 r:로그 없는 버전
			새로운 배속 라이브러리 버전(Sonic)으로 G3 마시멜로우에서 소리끊김 현상이 없어졌으며
			SoundTouch에 비해 음질이 좋은 듯 합니다.
	Cache  : 1.7.6(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.2
	AAR    : 2.8

변경된 라이브러리
	AAR

----------------------------------------------------------------------------------------

2016-01-21

1. ICS에서 죽는 현상 수정

2. MediaPlayerBase.OnInfoListener에 public void onFrameDrop(MediaPlayerBase mp) 추가

Version
	Server : 1.3.6(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.10(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.6(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.2
	AAR    : 2.7

변경된 라이브러리
	AAR

----------------------------------------------------------------------------------------

2016-01-06

1. 단기탭에서 고배속 & HW Code 연동 시 끊김 현상 제거
	현재 시간을 가져올 때 smooth하게 올라가도록 수정(마시멜로우 코드 참조)

2. 중복 재생 차단 버그 수정
	중복 재생 차단을 위해 playerId, userId를 등록하는데 네트워크 이슈로 실패 시
	재시도를 하지 않고 check하여 중복차단되는 현상이 있음.
	등록 실패 시 스트리밍 경우 3번 30초 주기로 재시도하고, 다운로드인 경우 30초 주기로 재시도함.

3. -8018(ERROR_CURLE_PARTIAL_FILE) 에러 메세지 추가
	서버에 요청한 사이즈와 응답으로 받은 데이터의 사이즈가 상이한 경우

4. 최초 실행 시 SW 코덱 우선으로 하면 다음 재생부터는 HW 코덱 연동 안되는 문제 수정
	지원 가능한 코덱 목록을 최초 실행 시 Global 변수로 구성을 하는데, 
	SW 코덱 우선이면 목록 구성 시에 HW 코덱은 제외해서 발생함.

Version
	Server : 1.3.6(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.8(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.6(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.2
	AAR    : 2.6

변경된 라이브러리
	AAR

----------------------------------------------------------------------------------------

2015-12-03

1. 인텔 계열에서 LMS Hash정보가 잘못 올라가는 경우

2. JNI연동 시 메모리 릭 제거

3. G4 Marshmallow에서 미지원되는 현상

Version
	Server : 1.3.5(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.7(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.5(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.1
	AAR    : 2.5

변경된 라이브러리
	AAR
	

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.3.5d";
String playVersion = "1.7.7d";
String cacheVersion = "1.7.5d";
String storageVersion = "1.7.1";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion);

----------------------------------------------------------------------------------------

2015-11-25

1. 롤리팝이상 NDK Codec 연동 시 맨 뒤로 Seek하면 에러코드 리턴하는 현상 수정

2. StorageManager에 releaseInstance함수 추가
	reference count를 고려해서 자동으로 finish를 호출함.

Version
	Server : 1.3.3(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.5(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.4(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.1
	Jar    : 2.5

변경된 라이브러리
	Jar
	

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.3.3d";
String playVersion = "1.7.5d";
String cacheVersion = "1.7.4d";
String storageVersion = "1.7.1";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion);

----------------------------------------------------------------------------------------

2015-11-19

1. 롤리팝이상 NDK Codec 연동 시 fail나는 오류 수정

Version
	Server : 1.3.3(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.3(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.3(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.1
	Jar    : 2.4

변경된 라이브러리
	Jar
	

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.3.3d";
String playVersion = "1.7.3d";
String cacheVersion = "1.7.3d";
String storageVersion = "1.7.1";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion);

----------------------------------------------------------------------------------------

2015-11-12

1. setDeviceASync시 UI Thread와 분리

2. setDevice(setDeviceASync)시 파일을 생성할 수 없는 위치로 호출 시 Hang되는 현상 수정

Version
	Server : 1.3.1(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7.1(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7.2(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7.1
	Jar    : 2.2

변경된 라이브러리
	Jar
	

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.3d";
String playVersion = "1.7d";
String cacheVersion = "1.7d";
String storageVersion = "1.7";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion);

----------------------------------------------------------------------------------------

2015-11-03

1. S2에서 SW Codec 연동 시 화면 색상 번짐

2. 오디오 컨텐츠를 재생하면 SW Codec으로 연동되도록 설정이 변경되는 버그 수정

3. bitrate이 큰 컨텐츠를 seek시 버퍼링이 오래 걸리는 현상 수정

4. 롤리팝이상인 경우 무조건 SW Codec연동되도록 수정

Version
	Server : 1.3(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.7(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.7(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.7
	Jar    : 1.12

변경된 라이브러리
	Jar
	

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.3d";
String playVersion = "1.7d";
String cacheVersion = "1.7d";
String storageVersion = "1.7";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion);

----------------------------------------------------------------------------------------

2015-10-28

1. G4에서 죽는 이슈 수정

2, 시스템 시간 조정 시 만료처리 안되는 이슈 수정
	A시간에 보고 난 후 만료시간(B)이 지난 후 볼 때는 만료처리가 되나
	A~B사이의 시간으로 되돌리면 재생되는 이슈 수정

Version
	Server : 1.2.2(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.6.2(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.6.2(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.6.2
	Jar    : 1.9

변경된 라이브러리
	Jar
	

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.2.2d";
String playVersion = "1.6.2d";
String cacheVersion = "1.6.2d";
String storageVersion = "1.6.2";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion);

----------------------------------------------------------------------------------------

2015-10-20

1. -8682 에러 문구 추가

2, 네트워크 타임아웃 설정(StorageManager는 재시도 회수 파라메터도 있음) API 추가

Version
	Server : 1.2(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.6(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.6(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.6
	Jar    : 1.8

변경된 라이브러리
	Jar
	libcrypto.so <--- 제거
	libcurl.so   <--- 제거
	

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.2d";
String playVersion = "1.6d";
String cacheVersion = "1.6d";
String storageVersion = "1.6";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion); 


DRM Callback, PlayCallback 시 메세리 처리 방식
1. 다운로드 중(DRM Callback : kind 1, kind 2)
	a. OnKollusStorageListener.onError시
		Sample Source의 DownloadActivity.java의 showErrorMessage함수 참조(150 라인)
		서비스 프로이저의 메세지가 있는 경우 KollusStorage.getLastError()가 Not NULL임

	b. KollusPlayerDRMListener의 onDRMInfo에서 KollusPlayerDRMListener.DCB_INFO_DELETE인 경우
		Sample Source의 DownloadActivity.java의 onDRMInfo 참조(233 라인)
		KollusContent.getServiceProviderMessage()
	
2. 플레이 중(Drm Callback : kind 3,       Play Callback : kind 1, kind 3)
	서비스 프로이저의 메세지가 있는 경우 MediaPlayerBase.getErrorString이 Not NULL임.
	(인자의 에러코드는 의미 없음, KollusStorage.getLastError와 같은 성격의 임) 
	샘플 소스의 PlayActivity.java의 onError함수 참조(484 라인)

----------------------------------------------------------------------------------------

2015-10-14

1. KollusStorage의 796라인에서 NullPointerException 오류 수정
	컨텐츠를 삭제 후 콜백이 오는 경우 발생

Version
	Server : 1.1(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.5.1(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.5(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.5
	Jar    : 1.7

변경된 라이브러리
	Jar

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.1d";
String playVersion = "1.5.1d";
String cacheVersion = "1.5d";
String storageVersion = "1.5";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion); 


----------------------------------------------------------------------------------------

2015-10-12

1. FFMpegExtractor.cpp에서 Crash 나는 현상 수정

Version
	Server : 1.1(r/d) d:로그 버전 r:로그 없는 버전
	Player : 1.5(r/d) d:로그 버전 r:로그 없는 버전
	Cache  : 1.5(r/d) d:로그 버전 r:로그 없는 버전
	Storage: 1.5
	Jar    : 1.6

변경된 라이브러리
	Jar

라이브러리 체크 호출 규칙
mLibraryChecker = new LibraryChecker(this, mOnCheckerListener);
String serverVersion = "1.1d";
String playVersion = "1.5d";
String cacheVersion = "1.5d";
String storageVersion = "1.5";
String ffmpegVersion = "1.0";
mLibraryChecker.check(serverVersion, playVersion, cacheVersion, storageVersion, ffmpegVersion); 


----------------------------------------------------------------------------------------

2015-10-08

1. 약전계에서 종료 시 Dead Lock이나 Crash될 수 있는 부분 제거

2. 북마크 속도 이슈
	BookmarkAdapter.java의 getView에서 Bitmap가져오는 부분을 add로 이동
	PlayActivity.java의 onBookmark부분 수정

Version
	Server : 1.1
	Player : 1.5
	Cache  : 1.5
	Storage: 1.5
	Jar    : 1.5

변경된 라이브러리
	Jar

----------------------------------------------------------------------------------------

2015-10-05

1. LMS 데이터 보안

2. HW Codec연동 시 갤럭시 그랜드 맥스에서 무한 죽는 현상 수정

3. 스토리지 매니저 초기화 ASync 함수 추가
	StorageManager의 initialize에서 했던 setDevice 제거로 파리메터 변경

Version
	Server : 1.1
	Player : 1.4
	Cache  : 1.4
	Storage: 1.4
	Jar    : 1.4

변경된 라이브러리
	Jar
	libcurl.so

----------------------------------------------------------------------------------------

2015-09-17

1. 다운로드할 라이브러리가 없는 경우 네트워크가 안되더라도 onDownloadCheckComplete가 호출되도록 수정

Version
	Server : 1.0
	Player : 1.3
	Cache  : 1.3
	Storage: 1.3
	Jar    : 1.4

----------------------------------------------------------------------------------------

2015-09-15

1. android 4.0미만 지원
   ICS미만인 경우 내장 플레이어 연동 (배속, 오디오 워터마크 지원 안됨)

2. 중복 차단 체크 주기 오류 수정
   300초 미만의 컨텐츠를 재생을 하면 체크 주기가 짧은 컨텐츠에 맞춰지는 오류 수정

3. 다운로드의 경우 LMS 정보의 start_at를 다운로드 당시의 서버시간이 아닌 재생시의 단말 시간을 내려 줌

4. API 인자 MediaContentKey 로 변경

5. LMS데이터에 checksum추가

Version
	Server : 1.0
	Player : 1.3
	Cache  : 1.3
	Storage: 1.3
	Jar    : 1.3

----------------------------------------------------------------------------------------

2015-07-20

1. 오디오 워터 마크가 적용된 컨텐츠의 경우 최대 배속을 1.5로 제한
   1.5배속을 초과하는 경우 오디오 워터 마크가 검출이 안됨

2. 중복 재생 체크 주기 변경
   300초가 안되는 컨텐츠의 경우 체크 주기를 Duration의 절반으로 함(최소 30초).

3. 캡쳐 차단 프로세스를 SDK로 이동

4. 소니 엑스페리아 Z1, Z2, Z3에 기본으로 내장되어 있는 켭쳐툴 추가

5. DRM Callback 1.6.22 적용

6. Player Callback 1.0.1 적용

----------------------------------------------------------------------------------------

2015-05-26

1. Arm 64bit Cpu 지원

----------------------------------------------------------------------------------------

2015-03-18

1. DRM V1.6 적용

2. 북마크 & 이어보기 고객사 서버 연동

3. 다운로드 컨텐츠 정보 추가(resolution, bitrate)

4. 플레이 중 wifi disable 시킬 경우 죽는 문제 수정
   wifi disable를 시킬 경우 wifi display detected receiver가 오는데 type cast 오류


변경된 라이브러리
kollusplayer_sdk.jar
	Player Version, Cache Version, Storage Version이 모두 "1.1"로 변경됨
	LibraryChecker의 check("1.1", "1.0", "1.1", "1.1");

----------------------------------------------------------------------------------------

2015-03-02

1. 필수 라이브러리 로드를 앱에서 SDK로 이동
   (예제의 InitActivity.java에서 System.load, System.loadLibrary제거)

2. 버전 정보 API 추가

변경된 라이브러리
kollusplayer_sdk.jar

----------------------------------------------------------------------------------------

2014-12-10

1. 롤리팝 지원

변경된 라이브러리
kollusplayer_sdk.jar

----------------------------------------------------------------------------------------

2014-11-19

1. SW Codec의 경우 Seek시 B-Frame 디코딩 오류

2. 이어보기시 첫 LMS 데이터 오류 수정

3. LMS의 JSON Data항목 추가
	playtime_percent
	last_play_at

4. Play Callback 추가

5. device name 변경
	Build.MODEL에서 Build.DEVICE+"/"+Build.MODEL로 변경

6. 북마크 INDEX 추가

변경된 라이브러리
libStorageMgr.so
libKollusPlayer_CacheManager.so
libKollusPlayer_ICS.so
libKollusPlayer_JB.so
libKollusPlayer_KK.so
kollusplayer_sdk.jar

----------------------------------------------------------------------------------------

2014-10-29

1. 배속 조정 후 정배속인 경우 배속 모듈 타는 버그 수정

2. 일부 단말 H/W Codec(google코덱만 있는 경우) 안되는 문제 수정

3. 다운로드 컨텐츠에 대해서도 진도률 적용
	네트워크가 안될 때는 저장 후 다음 재생 시 네트워크가 가능할 때 전송

4. 진도률 데이터를 pause 시 보내고 주기적으로는 보내지 않음.
   resume 시 보내고 다시 주기적으로 보냄.

변경된 라이브러리
libKollusPlayer_CacheManager.so
libKollusPlayer_ICS.so
libKollusPlayer_JB.so
libKollusPlayer_KK.so
kollusplayer_sdk.jar

----------------------------------------------------------------------------------------

2014-10-13

1. 동영상 상세 정보 새로운 스펙 적용

2. 다운로드 폴더 자동 완성

3. 반복적인 pause, resume시 죽는 현상

4. 소프트웨어 코덱으로 설정 후 seek를 여러번 시도 시 hang 걸림 현상 수정

변경된 라이브러리
libStorageMgr.so
libKollusPlayer_CacheManager.so
libKollusPlayer_ICS.so
libKollusPlayer_JB.so
libKollusPlayer_KK.so
kollusplayer_sdk.jar

----------------------------------------------------------------------------------------

2014-09-19

1. x86 단말 지원

2. 이미지 저장 기능 추가(Only SW Codec)

3. water mark 여부 api 추가

변경된 라이브러리
libStorageMgr.so
x86/libffmpeg.so
libKollusPlayer_CacheManager.so
libKollusPlayer_ICS.so
libKollusPlayer_JB.so
libKollusPlayer_KK.so
kollusplayer_sdk.jar

----------------------------------------------------------------------------------------

2014-08-28

1. 스킨에 로고가 없는 경우 로고패스에 "null"를 주는 현상 수정

2. 메모리 릭 제거

3. 컨텐츠 재생중 다운로드중에 네트웍 단절시 에러를 발생하지 않는 버그 수정

4. 게이트웨이 서버에 네트웍 요청 횟수 제한 처리

5. 자막 파싱 오류
   srt파일에 줄바꿈이 2번이상 있는 경우

6. Playback Complete시 N-Screen정보 잘못 올리는 버그 수정

7. Download Contents 상세정보 URL 추가

8. MediaInfo에 TV-Out Disable flag 추가

9. 킷캣에서 외장메모리 지원

10. AudioOnly인 경우 Seek 안되는 문제 수정

11. 초기 버퍼링에 걸린 시간 서버에 전송

----------------------------------------------------------------------------------------

2014-07-02

1. 폴더 삭제시 같은 레벨에 있는 파일도 삭제되는 버그 수정

2. 사운드 모노로 나오는 현상 수정

3. ffmpeg 라이브러리를 기본 앱에서 빼고 다른 앱으로 변경
    String libraryPath = "";
    Cpuinfo cpuInfo = Cpuinfo.getInstance();
    String cpuName = cpuInfo.getCpuName().toLowerCase();
    if(cpuName.startsWith("armv7")) {
    	if(cpuInfo.hasFeature("neon"))
    		libraryPath = "/data/data/com.kollus.ffmpeg.v7.neon/lib";
        else
        	libraryPath = "/data/data/com.kollus.ffmpeg.v7/lib";
    }
    else if(cpuName.startsWith("armv6")) {
    	if(cpuInfo.hasFeature("vfp"))
    		libraryPath = "/data/data/com.kollus.ffmpeg.v6.vfp/lib";
        else
        	libraryPath = "/data/data/com.kollus.ffmpeg.v6/lib";
    }
    
    mMediaPlayer = new MediaPlayer(mContext, mVideoWindowImpl);
    mMediaPlayer.initialize(libraryPath);

4. 스트리밍 시 빈번한 re-connection 제거

5. 다운로드 시 단말 개수 제한 체크

6. 스킨 적용된 컨텐츠를 다운로드 받은 경우, 다운로드 리스트에서 재생 시에도 스킨 적용

7. 중복 차단 팝업 시 백단에서 여전히 재생이 되는 현상 수정

8. 진도 관리 내부 항목 추가
    LMS {JSON_DATA}에 content_provider_key, start_at, device, OS, OS Version 항목 추가

9. logo파일을 내부에서 다운 받아서 로컬 패스로 넘겨줌

10. 자막이 있는 경우 sdk에서 처리
    addTimedTextSource를 호출하지 않아도 됨