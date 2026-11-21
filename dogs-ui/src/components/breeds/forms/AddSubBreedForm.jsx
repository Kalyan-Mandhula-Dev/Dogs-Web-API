import { useState } from 'react';
import '../../../styles/popup.css';
import '../../../styles/add-breed.css';
import ApiService from '../../../service/ApiService';

export default function AddSubBreedForm(props) {

    const [subBreedName, setSubBreedName] = useState("");
    const [subBreedNameError, setSubBreedNameError] = useState("");

    const handleSaveSubBreed = async () => {

        const trimmedSubBreedName = subBreedName.trim();

        if (!trimmedSubBreedName) {
            setSubBreedName("");
            setSubBreedNameError("Sub-breed name cannot be blank!!");
            return;
        }

        const response = await ApiService.post(`${props.SUBBREEDS_ENDPOINT}`, {
            name: trimmedSubBreedName
        })


        if (response.success) {

            let data = response.data

            if (!props.hasMore) {
                props.setSubBreeds([...props.subBreeds, data]);
            }

            props.setShowAddPopUp(false)
        } else {
            alert(response.message);
        }

        setSubBreedNameError("");
    };

    return (<><div className="action-popup">
        <div className="action-breed-form">
            <h3>Add Sub-breed 🐾</h3>

            <label>Sub Breed Name</label>
            <input
                type="text"
                placeholder={subBreedNameError ? subBreedNameError : "Enter sub-breed name"}
                value={subBreedName}
                onChange={(e) => setSubBreedName(e.target.value)}
            />

            <div className="popup-actions">
                <button
                    className="cancel-btn"
                    onClick={() => props.setShowAddPopUp(false)}
                >
                    Cancel
                </button>

                <button
                    className="confirm-btn"
                    onClick={handleSaveSubBreed}
                >
                    Save
                </button>
            </div>
        </div>
    </div></>);

}