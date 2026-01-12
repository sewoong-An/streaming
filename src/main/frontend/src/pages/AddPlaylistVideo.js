import { useEffect, useState } from "react";
import { authApi } from "../api/api";
import styles from "styles/page/AddPlaylistVideo.module.css";

function AddPlaylistVideo({ videoId, close }) {
  const [playlist, setPlaylist] = useState([]);

  const getPlayList = async () => {
    await authApi.get("/api/playlist/getPlaylist").then((response) => {
      setPlaylist(response.data.data);
    });
  };

  const add = (playlistId) => {
    authApi
      .post("/api/playlist/addPlaylistVideo", {
        playlistId: playlistId.toString(),
        videoId: videoId,
      })
      .then((response) => {
        close();
      });
  };

  useEffect(() => {
    getPlayList().then();
  }, []);

  return (
    <div>
      {playlist.map((item) => (
        <div className={styles.playlistBox}>
          <div
            className={styles.title}
            onClick={() => {
              add(item.playlistId);
            }}
            title={item.playlistTitle}
          >
            {item.playlistTitle}
          </div>
        </div>
      ))}
    </div>
  );
}

export default AddPlaylistVideo;
