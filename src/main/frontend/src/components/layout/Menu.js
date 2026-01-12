import styles from "styles/layout/Menu.module.css";
import { Link, useLocation } from "react-router-dom";
import MenuItem from "./MenuItem";
import iconHome from "icons/icon-home.png";
import iconHistory from "icons/icon-history.png";
import iconMyVideo from "icons/icon-myVideo.png";
import iconPlaylist from "icons/icon-playlist.png";
import iconSetting from "icons/icon-setting.png";
import iconSubscribe from "icons/icon-subscribe.png";

function Menu({ subscribeList }) {
  const pathName = useLocation().pathname;

  const defaultMenus = [
    { name: "홈", path: "/", icon: iconHome },
    { name: "구독", path: "/subscribe", icon: iconSubscribe },
    { name: "시청 기록", path: "/history", icon: iconHistory },
    { name: "내 동영상", path: "/myVideos", icon: iconMyVideo },
    { name: "재생목록", path: "/myPlaylist", icon: iconPlaylist },
  ];

  const addMenus = [{ name: "설정", path: "/setting", icon: iconSetting }];

  return (
    <div className={styles.menu}>
      <div>
        {defaultMenus.map((menu, index) => {
          return (
            <Link to={menu.path} key={index}>
              <MenuItem
                menu={menu}
                isActive={pathName === menu.path ? true : false}
              />
            </Link>
          );
        })}
      </div>
      <div className={styles.subscribeBox}>
        {subscribeList.length > 0 ? (
          <div>
            <div className={styles.sub}>구독</div>
            {subscribeList.map((sub) => (
              <Link
                to={"/channel/" + sub.channelHandle}
                key={sub.channelHandle}
              >
                <MenuItem
                  channel={sub}
                  isActive={
                    pathName.includes("/channel/" + sub.channelHandle)
                      ? true
                      : false
                  }
                />
              </Link>
            ))}
          </div>
        ) : null}
      </div>
      <div>
        {addMenus.map((menu, index) => {
          return (
            <Link to={menu.path} key={index + defaultMenus.length}>
              <MenuItem
                menu={menu}
                isActive={pathName === menu.path ? true : false}
              />
            </Link>
          );
        })}
      </div>
    </div>
  );
}

export default Menu;
