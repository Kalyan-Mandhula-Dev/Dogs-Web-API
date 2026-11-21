import { useState, useEffect } from 'react';
import '../../../styles/popup.css';
import ApiService from '../../../service/ApiService';

export default function EditSubBreedForm(props) {

    const [subBreedName, setSubBreedName] = useState("");
    const [subBreedNameError, setSubBreedNameError] = useState("");

    useEffect(() => {
        setSubBreedName(props.selectedSubBreed.name);
    }, [])

    const handleSaveSubBreed = async () => {

        if (!subBreedName.trim()) {
            setSubBreedName("");
            setSubBreedNameError("Sub-breed name cannot be blank!!");
            return;
        }

        const response = await ApiService.put(`${props.SUBBREEDS_ENDPOINT}/${props.selectedSubBreed.id}`, {
            name: subBreedName,
            version: props.selectedSubBreed.version
        });

        if (response.success) {
            let data = response.data
            props.setSubBreeds(prevSubBreeds => prevSubBreeds.map(subBreed => subBreed.id === data.id ? data : subBreed));
            // alert("Sub-breed updated successfully!!");
            props.setShowEditPopUp(false)
        } else {
            alert(response.message);
        }

        setSubBreedNameError("");
    };

    return (<><div className="action-popup">
        <div className="action-breed-form">
            <h3>Edit Sub-breed 🐶</h3>

            <input type="text"
                value={subBreedName}
                onChange={(e) => setSubBreedName(e.target.value)}
                placeholder={subBreedNameError ? subBreedNameError : "Enter sub-breed name"}
            />

            <div className="popup-actions">
                <button className="cancel-btn" onClick={() => props.setShowEditPopUp(false)}>
                    Cancel
                </button>
                <button className="confirm-btn" onClick={handleSaveSubBreed}>
                    Save
                </button>
            </div>
        </div>
    </div></>);

}