import { useState } from 'react';
import '../styles/popup.css';
import '../styles/add-breed.css';

export default function AddBreedForm(props) {

    const [breedName, setBreedName] = useState("");
    const [subBreedName, setSubBreedName] = useState("");
    const [subBreeds, setSubBreeds] = useState([]);
    const [breedNameError, setBreedNameError] = useState("");
    const [subBreedNameError, setSubBreedNameError] = useState("");

    const handleAddSubBreed = () => {
        if (!subBreedName.trim()) {
            setSubBreedName("");
            setSubBreedNameError("Sub-breed name cannot be blank!!");
            return;
        }

        setSubBreeds([...subBreeds, subBreedName.trim()]);
        setSubBreedName("");
        setSubBreedNameError("");
    };

    const handleRemoveSubBreed = (index) => {
        setSubBreeds(subBreeds.filter((subBreed, i) => i !== index));
    };

    const handleSaveBreed = async () => {
        console.log({
            name: breedName,
            subBreeds: subBreeds
        });

        if (!breedName.trim()) {
            setBreedName("");
            setBreedNameError("Breed name cannot be blank!!");
            return;
        }

        let response = await fetch(`${props.BREEDS_API_URL}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: breedName,
                subBreeds: subBreeds
            })
        });

        console.log(response)
        let data = await response.json();
        console.log(data);

        if (response.status === 201) {
            if (!props.hasMore) {
                props.setBreeds([...props.breeds, data]);
            }
            // alert("Breed created successfully!!");
            props.setShowAddPopUp(false)
        } else {
            alert(data.message);
        }

        setBreedNameError("");
    };

    return (<><div className="action-popup">
        <div className="action-breed-form">
            <h3>Add Breed 🐾</h3>

            <label>Breed Name</label>
            <input
                type="text"
                placeholder={breedNameError ? breedNameError : "Enter breed name"}
                value={breedName}
                onChange={(e) => setBreedName(e.target.value)}
            />

            <label>Sub-breeds</label>
            <input
                type="text"
                placeholder={subBreedNameError ? subBreedNameError : "Enter sub-breed name"}
                value={subBreedName}
                onChange={(e) => setSubBreedName(e.target.value)}
            />

            <button className="add-subbreed-btn" onClick={handleAddSubBreed}>
                + Add Sub-breed
            </button>

            <ul className="subbreed-list">
                {subBreeds.map((name, index) => (
                    <li key={index}>
                        <button className="remove-subbreed-btn">
                            {name}
                            <span onClick={() => handleRemoveSubBreed(index)}>🗑️</span>
                        </button>
                    </li>
                ))}
            </ul>

            <div className="popup-actions">
                <button
                    className="cancel-btn"
                    onClick={() => props.setShowAddPopUp(false)}
                >
                    Cancel
                </button>

                <button
                    className="confirm-btn"
                    onClick={handleSaveBreed}
                >
                    Save
                </button>
            </div>
        </div>
    </div></>);

}