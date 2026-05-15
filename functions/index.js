/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {setGlobalOptions} = require("firebase-functions/v2");
// const {onRequest} = require("firebase-functions/v2/https");
const {onDocumentCreated} = require("firebase-functions/v2/firestore");
// const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");
admin.initializeApp();

// For cost control, you can set the maximum number of containers that can be
// running at the same time. This helps mitigate the impact of unexpected
// traffic spikes by instead downgrading performance. This limit is a
// per-function limit. You can override the limit for each function using the
// `maxInstances` option in the function's options, e.g.
// `onRequest({ maxInstances: 5 }, (req, res) => { ... })`.
// NOTE: setGlobalOptions does not apply to functions using the v1 API. V1
// functions should each use functions.runWith({ maxInstances: 10 }) instead.
// In the v1 API, each function can only serve one request per container, so
// this will be the maximum concurrent request count.
setGlobalOptions({maxInstances: 10});

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

exports.sendMessageNotification = onDocumentCreated(
    "chat/{chatId}/messages/{messageId}", async (event) => {
      const message = event.data.data();
      const chatId = event.params.chatId;

      const db = admin.firestore();

      const senderId = message.senderId;
      const text = message.msgText;

      // chat document
      const chatDoc = await db.collection("chat").doc(chatId).get();

      if (!chatDoc.exists) {
        console.log("Chat document not found");
        return null;
      }

      const participants = chatDoc.data().participants;

      // receiverid
      const receiverId = participants.find((id) => id !== senderId);

      // receiver user info
      const userDoc = await db.collection("users").doc(receiverId).get();

      if (!userDoc.exists) {
        console.log("receiver not found");
        return null;
      }

      const token = userDoc.data().fcm_token;
      if (!token) {
        console.log("receiver has no fcm token");
        return null;
      }

      const payload = {
        notification: {
          title: "New Message",
          body: text,
        },
        token: token,
      };

      await admin.messaging().send(payload);

      console.log("Notification sent");

      return null;
    });
