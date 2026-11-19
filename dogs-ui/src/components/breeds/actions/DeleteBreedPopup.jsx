import '../../../styles/popup.css';
import '../../../styles/delete-breed.css';

export default function DeleteBreedPopup(props) {

    const handleDeleteBreed = async () => {

        let response = await fetch(`${props.BREEDS_API_URL}/${props.selectedBreed.id}`, {
            method: "DELETE"
        });

        console.log(response)

        if (response.status === 204) {
            props.setBreeds(prevBreeds => prevBreeds.filter(breed => breed.id !== props.selectedBreed.id));
            // alert("Breed deleted successfully!!");
            props.setShowConfirmDeletePopUp(false)
        } else {
            alert(data.message);
        }

    };

    return (<><div className="action-popup">
        <div className="action-breed-form">
            <h3>Are you sure? </h3>

            <p>
                Are you sure you want to delete this item?
                This will also delete all associated sub-breeds.
            </p>

            <div className="popup-actions">
                <button className="cancel-btn" onClick={() => props.setShowConfirmDeletePopUp(false)}>
                    Cancel
                </button>

                <button className="confirm-btn" onClick={handleDeleteBreed}>
                    Delete
                </button>
            </div>
        </div>
    </div></>);

}