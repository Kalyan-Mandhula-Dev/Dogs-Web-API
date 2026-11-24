import { useState } from "react"
import '../../../styles/popup.css';
import ApiService from "../../../service/ApiService";


export default function ImportFilePopup(props) {

    const [file, setFile] = useState(null);

    const handleImport = async () => {

        if (!file) return;

        let formData = new FormData();
        formData.append("file", file);

        let response = await ApiService.post(`${props.BREEDS_ENDPOINT}/import`, formData)

        if (response.success) {
            const { imported, skipped, skippedBreeds } = response.data;
            let summary = `Imported ${imported} new breed(s). Skipped ${skipped} existing breed(s).`;
            if (skipped > 0) {
                summary += `\n\nSkipped: ${skippedBreeds.join(", ")}`;
            }
            alert(summary)
            await props.refreshBreeds();
        } else {
            alert(response.message)
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

            <p>Files supported: JSON</p>

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