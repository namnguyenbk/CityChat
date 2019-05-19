
'use strict'
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}')
    .onWrite((change, context) => {
        const user_id = context.params.user_id;
        const notification_id = context.params.notification_id;

        if (!change.after.exists()) {
            return console.log('A notification has been deleted from the database', notification_id);

        }

        const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');
        return fromUser.then(fromUserResult => {
            const fromUserId = fromUserResult.val().from;
            console.log('You have new notification from : ' + fromUserId);

            const userQuery = admin.database().ref(`users/${fromUserId}/name`).once('value');
            const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');


            return Promise.all( [userQuery, deviceToken]).then( result =>{
                    const userName = result[0].val();
                    const token = result[1].val();
                    const payload = {
                        notification: {
                            title: "Friend request",
                            body:  `${userName.name} has sent  you request`,
                            icon: "default",
                            click_action : ".btl.lapitcaht.TARGET_NOTIFICATION"
                        },
                        data : {
                            from_user_id : fromUserId
                        }
                    }
                    return admin.messaging().sendToDevice(token_id, payload).then(response => {
                        console.log('This was the notification Feature');

                    });
            });
            // return userQuery.then(userResult => {
            //     const userName = userResult.val();
            //     return deviceToken.then(result => {

                    // const token_id = result.val();
                    // const payload = {
                    //     notification: {
                    //         title: "Friend request",
                    //         body:  `${userName.name} has sent  you request`,
                    //         icon: "default",
                    //         click_action : ".btl.lapitcaht.TARGET_NOTIFICATION"
                    //     },
                    //     data : {
                    //         from_user_id : fromUserId
                    //     }
                    // }
                    // return admin.messaging().sendToDevice(token_id, payload).then(response => {
                    //     console.log('This was the notification Feature');

                    // });
                });
            });
    //     });
    // });
