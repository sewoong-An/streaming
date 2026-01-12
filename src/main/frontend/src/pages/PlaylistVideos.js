import styles from "styles/page/PlaylistVideos.module.css";
import { useOutletContext, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { authApi } from "../api/api";
import * as common from "../common";
import VideoCardHorizon from "../components/common/VideoCardHorizon";

function PlaylistVideos() {
  const { id } = useParams();
  const [userInfo, setUserInfo] = useState(null);
  const [playlistInfo, setPlaylistInfo] = useState(null);
  const [playlistVideos, setPlaylistVideos] = useState([]);
  const { winSize } = useOutletContext();

  const getUserInfo = async () => {
    await authApi.get("/api/member/getUserInfo").then((response) => {
      setUserInfo(response.data.data);
    });
  };

  const getPlaylistInfo = async () => {
    await authApi
      .get("/api/playlist/getPlaylistInfo/" + id)
      .then((response) => {
        setPlaylistInfo(response.data.data);
      });
  };

  const getPlaylistVideos = async () => {
    await authApi
      .get("/api/playlist/getPlaylistVideos/" + id)
      .then((response) => {
        setPlaylistVideos(response.data.data);
      });
  };

  const deletePlaylistVideo = async (videoId) => {
    await authApi
      .delete("/api/playlist/" + id + "/delete/" + videoId)
      .then((response) => {});
  };

  useEffect(() => {
    getUserInfo().then(() => {
      getPlaylistInfo().then(() => {
        getPlaylistVideos().then();
      });
    });
  }, [id]);

  return (
    <div className={winSize ? styles.content : styles.miniContent}>
      {playlistInfo !== null ? (
        <div className={styles.playlistInfoBox}>
          <div className={styles.playlistImageBox}>
            <img
              className={styles.playlistImage}
              src={playlistInfo.playlistImageUrl}
            />
            <div className={styles.imageGradient}></div>
          </div>
          <div className={styles.playlistTitle}>
            {playlistInfo.playlistTitle}
          </div>
          <div className={styles.videoCountAndUpdateDt}>
            동영상 {playlistInfo.playlistVideoCount}개 •{" "}
            {common.formattime(playlistInfo.playlistUpdateDt)} 업데이트됨
          </div>
        </div>
      ) : null}
      <div
        className={
          winSize ? styles.playlistVideoBox : styles.miniPlaylistVideoBox
        }
      >
        {playlistVideos.map((video) => (
          <div className={styles.video}>
            <VideoCardHorizon
              videoInfo={video}
              deleteFun={
                userInfo.channelCode === playlistInfo.channel.channelCode
                  ? () => {
                      deletePlaylistVideo(video.videoId);
                    }
                  : undefined
              }
              imageSize={winSize ? "mini" : ""}
            />
          </div>
        ))}
      </div>
    </div>
  );
}

export default PlaylistVideos;
