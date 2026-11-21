import { useState, useEffect } from 'react';
import '../../../styles/popup.css';
import ApiService from '../../../service/ApiService';

export default function EditBreedForm(props) {

    const [breedName, setBreedName] = useState("");
    const [breedNameError, setBreedNameError] = useState("");

    useEffect(() => {
        setBreedName(props.selectedBreed.name);
    }, [])

    const handleSaveBreed = async () => {

        console.log({
            name: breedName
        });

        const trimmedBreedName = breedName.trim();

        if (!trimmedBreedName) {
            setBreedName("")
            setBreedNameError("Breed name cannot be blank!!");
            return;
        }

        const response = await ApiService.put(`${props.BREEDS_ENDPOINT}/${props.selectedBreed.id}`, {
            name: trimmedBreedName,
            version: props.selectedBreed.version
        })

        if (response.success) {
            let data = response.data;
            props.setBreeds(prevBreeds => prevBreeds.map(breed => breed.id === data.id ? data : breed));
        } else {
            alert(response.message)
        }

        props.setShowEditPopUp(false)
        setBreedNameError("");
    };

    return (<><div className="action-popup">
        <div className="action-breed-form">
            <h3>Edit Breed 🐶</h3>

            <input type="text"
                value={breedName}
                onChange={(e) => setBreedName(e.target.value)}
                placeholder={breedNameError ? breedNameError : "Enter breed name"}
            />

            <div className="popup-actions">
                <button className="cancel-btn" onClick={() => props.setShowEditPopUp(false)}>
                    Cancel
                </button>
                <button className="confirm-btn" onClick={handleSaveBreed}>
                    Save
                </button>
            </div>
        </div>
    </div></>);

}