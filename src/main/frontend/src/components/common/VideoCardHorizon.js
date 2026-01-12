import styles from "styles/common/VideoCardHorizon.module.css";
import * as common from "common.js";
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import DeleteIcon from "./DeleteIcon";

function VideoCardHorizon({ videoInfo, deleteFun, imageSize }) {
  const [isHover, setIsHover] = useState(false);
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
      className={styles.videoBox}
      onMouseOver={() => {
        setIsHover(true);
      }}
      onMouseOut={() => {
        setIsHover(false);
      }}
      onClick={goWatch}
    >
      <div className={imageSize === "mini" ? styles.miniImgBox : styles.imgBox}>
        <img src={videoInfo.thumbnailUrl} className={styles.videoImg} />
        <div className={styles.runningTime}>
          {common.formatRunningTime(videoInfo.runningTime)}
        </div>
      </div>
      <div
        className={
          imageSize === "mini"
            ? deleteFun !== undefined
              ? styles.miniInfoBox
              : styles.noDeleteMiniInfoBox
            : deleteFun !== undefined
            ? styles.infoBox
            : styles.noDeleteInfoBox
        }
      >
        <div className={styles.titleAndDeleteBtn}>
          <div className={styles.title} title={videoInfo.title}>
            {videoInfo.title}
          </div>
          {deleteFun !== undefined && isHover ? (
            <div className={styles.deleteBtn}>
              <DeleteIcon id={videoInfo.videoId} delFun={deleteFun} />
            </div>
          ) : null}
        </div>
        <div>
          <div className={styles.channel} onClick={goChannel}>
            {videoInfo.channel.channelName}
          </div>
        </div>
        <div className={styles.viewsAndDt}>
          조회수 {common.formatViews(videoInfo.views)} •{" "}
          {common.formattime(videoInfo.createdDt)}
        </div>
      </div>
    </div>
  );
}

export default VideoCardHorizon;
