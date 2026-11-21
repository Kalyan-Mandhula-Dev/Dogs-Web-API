import '../../../styles/popup.css';
import '../../../styles/delete-breed.css';
import ApiService from '../../../service/ApiService';

export default function DeleteSubBreedPopup(props) {

    const handleDeleteSubBreed = async () => {

        let response = await ApiService.delete(`${props.SUBBREEDS_ENDPOINT}/${props.selectedSubBreed.id}/${props.selectedSubBreed.version}`);

        if (response.success) {
            props.setSubBreeds(prevSubBreeds => prevSubBreeds.filter(subBreed => subBreed.id !== props.selectedSubBreed.id));
        } else {
            alert(response.message);
        }
        props.setShowConfirmDeletePopUp(false)
    };

    return (<><div className="action-popup">
        <div className="action-breed-form">
            <h3>Are you sure? </h3>

            <p>
                Are you sure you want to delete this item?
            </p>

            <div className="popup-actions">
                <button className="cancel-btn" onClick={() => props.setShowConfirmDeletePopUp(false)}>
                    Cancel
                </button>

                <button className="confirm-btn" onClick={handleDeleteSubBreed}>
                    Delete
                </button>
            </div>
        </div>
    </div></>);

}