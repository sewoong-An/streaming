import styles from "styles/layout/Footer.module.css";
import iconHome from "../../icons/icon-home.png";
import iconSubscribe from "../../icons/icon-subscribe.png";
import iconHistory from "../../icons/icon-history.png";
import iconMyVideo from "../../icons/icon-myVideo.png";
import iconPlaylist from "../../icons/icon-playlist.png";
import { Link, useLocation } from "react-router-dom";

function Footer() {
  const pathName = useLocation().pathname;
  const defaultMenus = [
    { name: "홈", path: "/", icon: iconHome },
    { name: "구독", path: "/subscribe", icon: iconSubscribe },
    { name: "시청 기록", path: "/history", icon: iconHistory },
    { name: "내 동영상", path: "/myVideos", icon: iconMyVideo },
    { name: "재생목록", path: "/myPlaylist", icon: iconPlaylist },
  ];
  return (
    <footer className={styles.footer}>
      {defaultMenus.map((m, index) => (
        <Link to={m.path} key={index}>
          <div
            className={
              pathName === m.path ? styles.activeEachMenu : styles.eachMenu
            }
          >
            <img className={styles.icon} src={m.icon} />
            <div className={styles.menuName}>{m.name}</div>
          </div>
        </Link>
      ))}
    </footer>
  );
}

export default Footer;
