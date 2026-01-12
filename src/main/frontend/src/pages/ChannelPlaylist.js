import { useOutletContext } from "react-router-dom";
import { useEffect, useState } from "react";
import { authApi } from "../api/api";
import { Col, Row } from "react-bootstrap";
import PlaylistCard from "../components/common/PlaylistCard";
import styles from "styles/page/ChannelPlaylist.module.css";

function ChannelPlaylist() {
  const { handle, contentsRef } = useOutletContext();
  const [playlist, setPlaylist] = useState([]);
  const [offset, setOffset] = useState(0);

  useEffect(() => {
    authApi
      .get("/api/playlist/getPlaylistByHandle/" + handle)
      .then((response) => {
        setPlaylist(response.data.data);
      });
  }, [handle]);

  useEffect(() => {
    if (playlist.length === 0) return;

    contentsRef.current.addEventListener("scroll", function () {
      const addScrollTop =
        (contentsRef.current.scrollHeight - contentsRef.current.offsetHeight) *
        0.9;

      if (contentsRef.current.scrollTop >= addScrollTop) {
        if (playlist.length >= offset + 12) {
          setOffset((curr) => curr + 12);
        }
      }
    });
  }, [playlist]);

  return (
    <div className={styles.contentsBox}>
      <Row xs={1} sm={1} md={2} lg={3} xxl={4} className="g-4">
        {playlist.slice(0, Math.min(playlist.length, offset + 12)).map((p) => (
          <Col key={p.playlistId}>
            <PlaylistCard info={p} />
          </Col>
        ))}
      </Row>
    </div>
  );
}

export default ChannelPlaylist;
