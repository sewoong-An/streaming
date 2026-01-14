import cookie from "react-cookies";
import styles from "styles/common/CustomVideo.module.css";
import { authApi } from "../../api/api";

function CustomVideo({ videoUrl, thumbnailUrl }) {
  const playVideo = () => {
    const videoPlayer = document.getElementsByTagName("video")[0];
    const userVolume = cookie.load("volume");
    if (userVolume === undefined) {
      videoPlayer.volume = 0.2;
    } else {
      videoPlayer.volume = userVolume;
    }
  };

  const changeVolume = (e) => {
    cookie.save("volume", e.target.volume, {
      path: "/",
    });
  };

  return (
    <div className={styles.videoBox}>
      <video
        className={styles.video}
        key={videoUrl}
        src={videoUrl}
        controls
        controlsList="nodownload"
        playsInline={true}
        crossOrigin="anonymous" // CORS 요청 시 Range 헤더 전송을 원활하게 함
        onCanPlay={playVideo}
        onVolumeChange={changeVolume}
        poster={thumbnailUrl || null}
        preload="metadata"
      ></video>
    </div>
  );
}

export default CustomVideo;
