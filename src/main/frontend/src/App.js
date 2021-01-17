import './App.css';
import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { useDropzone } from 'react-dropzone'

const API_URL = "http://localhost:8080/api/v1/user-profile";

const UserProfiles = () => {
  const [userProfiles, setUserProfiles] = useState([]);

  const fetchUserProfiles = () =>
    axios.get(API_URL).then(({ data }) => setUserProfiles(data));

  useEffect(() => fetchUserProfiles(), []);

  return userProfiles.map((userProfile, index) => {
    return (
      <div key={index} id="profile-container">
        {
          userProfile.userProfileId
            ? <img src={`${API_URL}/${userProfile.userProfileId}/image/download`} alt="Profile" />
            : null
        }
        <h1>{userProfile.username}</h1>
        <p>{userProfile.userProfileId}</p>
        <Dropzone {...userProfile} />
      </div>

    )
  })
}

function Dropzone({ userProfileId }) {
  const onDrop = useCallback((acceptedFiles, userProfileId) => {
    const headers = { "Content-Type": "multipart/form-data" };
    const formData = new FormData();
    formData.append("file", acceptedFiles[0]);

    axios
      .post(`${API_URL}/${userProfileId}/image/upload`, formData, { headers })
      .then(() => console.log("File uploaded successfully"))
      .catch(err => console.log(err));

  }, []);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({ onDrop })

  return (
    <div id="dropzone-container" {...getRootProps()}>
      <input {...getInputProps()} />
      {
        isDragActive ?
          <p>Drop the files here ...</p> :
          <p>Drag 'n' drop some files here, or click to select files</p>
      }
    </div>
  )
}

function App() {
  return (
    <div className="App">
      <UserProfiles />
    </div>
  );
}

export default App;
