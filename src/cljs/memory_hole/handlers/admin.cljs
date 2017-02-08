(ns memory-hole.handlers.admin
  (:require [re-frame.core :refer [dispatch dispatch-sync reg-event-db]]
            [memory-hole.ajax :refer [ajax-error]]
            [ajax.core :refer [GET POST PUT]]))


;;users
(reg-event-db
  :admin/set-users
  (fn [db [_ users]]
    (assoc db :admin/users users)))

(reg-event-db
  :admin/search-for-users
  (fn [db [_ screenname]]
    (GET (str "/admin/users/" screenname)
         :handler #(dispatch [:admin/set-users (:users %)])
         :error-handler #(ajax-error %))
    db))

(reg-event-db
  :admin/set-user-info
  (fn [db [_ {:keys [user-id] :as user}]]
    (update db :admin/users
            (fn [users]
              (map #(if (= user-id (:user-id %)) user %) users)))))

(reg-event-db
  :admin/update-user-profile
  (fn [db [_ user]]
    (PUT "/admin/user"
         {:params        user
          :handler       #(dispatch [:admin/set-user-info (:user %)])
          :error-handler #(ajax-error %)})
    db))

(reg-event-db
  :admin/create-user-profile
  (fn [db [_ user]]
    (POST "/admin/user"
          {:params        user
           :error-handler #(ajax-error %)})
    db))

;;groups
(reg-event-db
  :admin/add-group-info
  (fn [db [_ group]]
    (update db :groups #(conj % group))))

(reg-event-db
  :admin/create-group
  (fn [db [_ group]]
    (POST "/admin/group"
          {:params        group
           :handler       #(dispatch [:admin/add-group-info (:group %)])
           :error-handler #(ajax-error %)})
    db))

(reg-event-db
  :admin/set-group-users
  (fn [db [_ group-name users]]
    (assoc-in db [:group-users group-name] users)))

(reg-event-db
  :admin/load-group-users
  (fn [db [_ group-name]]
    (GET (str "/admin/users/group/" group-name)
         :handler #(dispatch [:admin/set-group-users group-name (:users %)])
         :error-handler #(ajax-error %))
    db))