import { useState, useEffect } from 'react';
import '../../../styles/popup.css';

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

        if (!breedName.trim()) {
            setBreedName("")
            setBreedNameError("Breed name cannot be blank!!");
            return;
        }

        let response = await fetch(`${props.BREEDS_API_URL}/${props.selectedBreed.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: breedName,
                version: props.selectedBreed.version
            })
        });

        console.log(response)
        let data = await response.json();
        console.log(data);

        if (response.status === 200) {
            props.setBreeds(prevBreeds => prevBreeds.map(breed => breed.id === data.id ? data : breed));
            // alert("Breed updated successfully!!");
            props.setShowEditPopUp(false)
        } else {
            alert(data.message);
        }

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