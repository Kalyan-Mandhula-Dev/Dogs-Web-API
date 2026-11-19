import { useState } from "react"
import '../styles/popup.css';


export default function ImportFilePopup(props) {

    const [file, setFile] = useState(null);

    const handleImport = async () => {

        if (!file) return;

        console.log("importing breeds", file);
        let formData = new FormData();
        formData.append("file", file);

        let response = await fetch(`${props.BREEDS_API_URL}/import`, {
            method: "POST",
            body: formData
        })

        console.log(response)
        let data = await response.json();
        console.log(data);

        if (response.status === 201) {
            alert("Breeds imported successfully!!")
            await props.refreshBreeds();
        } else {
            alert(data.message);
        }

        props.setShowImportPopUp(false);
    }

    return (<><div className="action-popup">
        <div className="action-breed-form">
            <h3>Import Breeds</h3>

            <div className="select-file-container">
                <input
                    type="file"
                    accept=".json"
                    onChange={(e) => setFile(e.target.files[0])}
                />

            </div>

            <div className="popup-actions">
                <button className="cancel-btn" onClick={() => props.setShowImportPopUp(false)}>
                    Cancel
                </button>

                <button className="confirm-btn" onClick={handleImport}>
                    Import
                </button>
            </div>
        </div>
    </div></>)
}