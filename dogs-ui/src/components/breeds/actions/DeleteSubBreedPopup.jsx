import '../../../styles/popup.css';
import '../../../styles/delete-breed.css';

export default function DeleteSubBreedPopup(props) {

    const handleDeleteSubBreed = async () => {

        let response = await fetch(`${props.SUBBREEDS_API_URL}/${props.selectedSubBreed.id}`, {
            method: "DELETE"
        });

        console.log(response)

        if (response.status === 204) {
            props.setSubBreeds(prevSubBreeds => prevSubBreeds.filter(subBreed => subBreed.id !== props.selectedSubBreed.id));
            // alert("Sub-breed deleted successfully!!");
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