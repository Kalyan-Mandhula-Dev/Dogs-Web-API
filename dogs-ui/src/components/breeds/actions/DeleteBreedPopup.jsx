import '../../../styles/popup.css';
import '../../../styles/delete-breed.css';
import ApiService from '../../../service/ApiService';

export default function DeleteBreedPopup(props) {

    const handleDeleteBreed = async () => {

        const response = await ApiService.delete(`${props.BREEDS_ENDPOINT}/${props.selectedBreed.id}`)

        if (response.success) {
            props.setBreeds(prevBreeds => prevBreeds.filter(breed => breed.id !== props.selectedBreed.id));
            props.setShowConfirmDeletePopUp(false)
        } else {
            alert(response.message)
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