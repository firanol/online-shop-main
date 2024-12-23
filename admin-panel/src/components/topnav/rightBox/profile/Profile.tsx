import { useContext } from "react";
import { images } from "../../../../constants";
import classes from "./Profile.module.scss";
import { useTranslation } from "react-i18next";
import { useUser } from "../../../../store/UserContext";

function Profile() {
  const { t } = useTranslation();
  const { user } = useUser();

  return (
    <div className={classes.profile}>
      <div className={classes.profile__avatar}>
        <img src={images.avt} alt="avatar" />
      </div>
      <div className={classes.profile__info}>
        <p className={classes.profile__userName}>{t(user?.name || "")}</p>
        {/* <span className={classes.profile__role}>{t("admin")}</span> */}
      </div>
    </div>
  );
}

export default Profile;