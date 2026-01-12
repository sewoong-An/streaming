import styles from "styles/common/VideoCard.module.css";
import * as common from "common.js";
import { useNavigate } from "react-router-dom";

function VideoCard({ videoInfo, effHover, viewChannel }) {
  const navigate = useNavigate();

  const goWatch = () => {
    navigate("/watch/" + videoInfo.videoId);
  };

  const goChannel = (e) => {
    e.stopPropagation();
    navigate("/channel/" + videoInfo.channel.channelHandle);
  };

  return (
    <div
      className={effHover === true ? styles.video : styles.noHvVideo}
      onClick={goWatch}
    >
      <div className={styles.videoHeader}>
        <img src={videoInfo.thumbnailUrl} className={styles.videoImg} />
        <div className={styles.runningTime}>
          {common.formatRunningTime(videoInfo.runningTime)}
        </div>
      </div>
      <div className={styles.videoBody}>
        {viewChannel === true ? (
          <img
            className={styles.channelImg}
            src={videoInfo.channel.channelImage}
          />
        ) : null}
        <div
          className={
            viewChannel === true ? styles.videoInfo : styles.noChVideoInfo
          }
        >
          <div className={styles.videoTitle} title={videoInfo.title}>
            {videoInfo.title}
          </div>
          <div>
            {viewChannel === true ? (
              <div onClick={goChannel} className={styles.channelName}>
                {videoInfo.channel.channelName}
              </div>
            ) : null}
            <div className={styles.viewsAndDt}>
              조회수 {common.formatViews(videoInfo.views)} •{" "}
              {common.formattime(videoInfo.createdDt)}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default VideoCard;
