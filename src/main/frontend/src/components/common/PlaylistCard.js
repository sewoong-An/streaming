import styles from "../../styles/common/PlaylistCard.module.css";
import iconEnter from "icons/icon-enter.png";
import * as common from "../../common";
import { useNavigate } from "react-router-dom";

function PlaylistCard({ info }) {
  const navigate = useNavigate();

  const goPlaylist = () => {
    navigate("/playlist/" + info.playlistId);
  };

  return (
    <div className={styles.playlist} onClick={goPlaylist}>
      <div className={styles.playlistHeader}>
        <img src={info.playlistImageUrl} className={styles.playlistImg} />
        <div className={styles.playlistInfo}>
          동영상 {info.playlistVideoCount}개
        </div>
        <div className={styles.hoverBox}>
          <img src={iconEnter} className={styles.hoverImage} />
        </div>
      </div>
      <div className={styles.playlistBody}>
        <div className={styles.playlistTitle}>{info.playlistTitle}</div>
        <div className={styles.updateDt}>
          {common.formattime(info.playlistUpdateDt)} 업데이트됨
        </div>
      </div>
    </div>
  );
}

export default PlaylistCard;
