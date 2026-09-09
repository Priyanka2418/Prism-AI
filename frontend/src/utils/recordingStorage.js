const DB_NAME = "PrismAIRecordingsDB";
const DB_VERSION = 1;
const STORE_NAME = "recordings";

function openDatabase() {
  return new Promise((resolve, reject) => {
    if (typeof window === "undefined" || !window.indexedDB) {
      reject(new Error("IndexedDB is not supported in this browser."));
      return;
    }

    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onupgradeneeded = (event) => {
      const db = event.target.result;
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME, { keyPath: "interviewId" });
      }
    };

    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });
}

/**
 * Saves a video/audio recording Blob for a given interview.
 * @param {string} interviewId
 * @param {Blob} blob
 * @returns {Promise<void>}
 */
export async function saveInterviewRecording(interviewId, blob) {
  if (!interviewId || !blob) return;

  const db = await openDatabase();
  return new Promise((resolve, reject) => {
    const transaction = db.transaction(STORE_NAME, "readwrite");
    const store = transaction.objectStore(STORE_NAME);

    const record = {
      interviewId,
      blob,
      mimeType: blob.type || "video/webm",
      size: blob.size,
      savedAt: new Date().toISOString(),
    };

    const request = store.put(record);
    request.onsuccess = () => resolve();
    request.onerror = () => reject(request.error);
  });
}

/**
 * Retrieves the recording Blob for a given interview.
 * @param {string} interviewId
 * @returns {Promise<Blob | null>}
 */
export async function getInterviewRecording(interviewId) {
  if (!interviewId) return null;

  try {
    const db = await openDatabase();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction(STORE_NAME, "readonly");
      const store = transaction.objectStore(STORE_NAME);
      const request = store.get(interviewId);

      request.onsuccess = () => {
        if (request.result && request.result.blob) {
          resolve(request.result.blob);
        } else {
          resolve(null);
        }
      };

      request.onerror = () => reject(request.error);
    });
  } catch (err) {
    console.warn("Failed to retrieve recording from IndexedDB:", err);
    return null;
  }
}

/**
 * Deletes the recording for a given interview.
 * @param {string} interviewId
 * @returns {Promise<void>}
 */
export async function deleteInterviewRecording(interviewId) {
  if (!interviewId) return;

  try {
    const db = await openDatabase();
    return new Promise((resolve, reject) => {
      const transaction = db.transaction(STORE_NAME, "readwrite");
      const store = transaction.objectStore(STORE_NAME);
      const request = store.delete(interviewId);

      request.onsuccess = () => resolve();
      request.onerror = () => reject(request.error);
    });
  } catch (err) {
    console.warn("Failed to delete recording:", err);
  }
}
