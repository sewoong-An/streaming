import styles from "styles/layout/Header.module.css";
import Logo from "icons/Logo.png";
import searchIcon from "icons/icon-search.png";
import backIcon from "icons/icon-back.png";
import logoutIcon from "icons/icon-logout.png";
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import cookie from "react-cookies";

function Header({ size }) {
  const isLogin = localStorage.getItem("isLogin");
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState();
  const [openSearch, setOpenSearch] = useState(false);

  const openSearchBar = () => {
    setOpenSearch(true);
  };

  const closeSearchBar = () => {
    setOpenSearch(false);
  };

  const changeSearchQuery = (e) => {
    setSearchQuery(e.target.value);
  };

  useEffect(() => {}, []);

  const logout = () => {
    localStorage.removeItem("accessToken");
    cookie.remove("refreshToken");
    localStorage.setItem("isLogin", "F");
  };

  return (
    <div>
      <header className={styles.header}>
        <div className={styles.logoArea}>
          <Link className={styles.logo} to={"/"}>
            <img className={styles.logo} src={Logo} />
          </Link>
        </div>
        <div className={styles.searchArea}>
          {size === "mini" ? (
            <button className={styles.searchBtn} onClick={openSearchBar}>
              <img src={searchIcon} />
            </button>
          ) : (
            <form
              className={styles.searchBox}
              action={"/search"}
              method={"get"}
            >
              <input
                value={searchQuery}
                type="text"
                name={"searchQuery"}
                className={styles.searchInput}
                onChange={changeSearchQuery}
                spellcheck="false"
                autocomplete="off"
              />
              <button type="submit" className={styles.searchBtn}>
                <img src={searchIcon} />
              </button>
            </form>
          )}
        </div>
        <div className={styles.accountArea}>
          <Link to={"/login"}>
            {isLogin === "T" ? (
              <img
                className={styles.logoutIcon}
                onClick={logout}
                src={logoutIcon}
              />
            ) : (
              <div>로그인</div>
            )}
          </Link>
        </div>
      </header>
      {size === "mini" && openSearch ? (
        <div className={styles.searchBar}>
          <button className={styles.searchBarBtn} onClick={closeSearchBar}>
            <img src={backIcon} />
          </button>
          <form
            className={styles.searchBarInputBox}
            action={"/search"}
            method={"get"}
          >
            <input
              value={searchQuery}
              type="text"
              name={"searchQuery"}
              className={styles.searchBarInput}
              onChange={changeSearchQuery}
              spellCheck="false"
              autoComplete="off"
            />
            <button type="submit" className={styles.searchBarBtn}>
              <img src={searchIcon} />
            </button>
          </form>
        </div>
      ) : null}
    </div>
  );
}

export default Header;
