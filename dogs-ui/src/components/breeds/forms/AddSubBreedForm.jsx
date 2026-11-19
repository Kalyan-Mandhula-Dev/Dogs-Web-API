import { useState } from 'react';
import '../../../styles/popup.css';
import '../../../styles/add-breed.css';

export default function AddSubBreedForm(props) {

    const [subBreedName, setSubBreedName] = useState("");
    const [subBreedNameError, setSubBreedNameError] = useState("");

    const handleSaveSubBreed = async () => {
        console.log({
            name: subBreedName
        });

        if (!subBreedName.trim()) {
            setSubBreedName("");
            setSubBreedNameError("Sub-breed name cannot be blank!!");
            return;
        }

        let response = await fetch(`${props.SUBBREEDS_API_URL}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: subBreedName
            })
        });

        console.log(response)
        let data = await response.json();
        console.log(data);

        if (response.status === 201) {
            if (!props.hasMore) {
                props.setSubBreeds([...props.subBreeds, data]);
            }
            // alert("Sub-breed created successfully!!");
            props.setShowAddPopUp(false)
        } else {
            alert(data.message);
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