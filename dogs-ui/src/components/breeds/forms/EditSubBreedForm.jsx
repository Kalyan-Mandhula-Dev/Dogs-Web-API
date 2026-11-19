import { useState, useEffect } from 'react';
import '../../../styles/popup.css';

export default function EditSubBreedForm(props) {

    const [subBreedName, setSubBreedName] = useState("");
    const [subBreedNameError, setSubBreedNameError] = useState("");

    useEffect(() => {
        setSubBreedName(props.selectedSubBreed.name);
    }, [])

    const handleSaveSubBreed = async () => {
        console.log({
            name: subBreedName
        });

        if (!subBreedName.trim()) {
            setSubBreedName("");
            setSubBreedNameError("Sub-breed name cannot be blank!!");
            return;
        }

        let response = await fetch(`${props.SUBBREEDS_API_URL}/${props.selectedSubBreed.id}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: subBreedName,
                version: props.selectedSubBreed.version
            })
        });

        console.log(response)
        let data = await response.json();
        console.log(data);

        if (response.status === 200) {
            props.setSubBreeds(prevSubBreeds => prevSubBreeds.map(subBreed => subBreed.id === data.id ? data : subBreed));
            // alert("Sub-breed updated successfully!!");
            props.setShowEditPopUp(false)
        } else {
            alert(data.message);
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