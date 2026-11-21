import { useState, useEffect } from 'react';
import '../../styles/breeds.css';
import { useNavigate } from 'react-router';
import AddBreedForm from './forms/AddBreedForm';
import EditBreedForm from './forms/EditBreedForm';
import DeleteBreedPopup from './actions/DeleteBreedPopup';
import ImportFilePopup from './actions/ImportFilePopup';
import ApiService from '../../service/ApiService';

export default function Breeds() {

    const navigate = useNavigate();
    const [breeds, setBreeds] = useState([]);
    const [showEditPopUp, setShowEditPopUp] = useState(false);
    const [showConfirmDeletePopUp, setShowConfirmDeletePopUp] = useState(false);
    const [showAddPopUp, setShowAddPopUp] = useState(false);
    const [showImportPopUp, setShowImportPopUp] = useState(false);
    const [selectedBreed, setSelectedBreed] = useState(null);
    const [lastId, setLastId] = useState(0);
    const [hasMore, setHasMore] = useState(false);
    const [loading, setLoading] = useState(true);

    const BREEDS_ENDPOINT = "/v1/breeds";


    const fetchBreeds = async () => {

        const response = await ApiService.get(BREEDS_ENDPOINT + `?lastId=${lastId}`)

        if (response.success) {

            let data = response.data
            console.log("Breeds : ", data)

            setBreeds(prev => [...prev, ...data]);

            if (data.length > 0) {
                setLastId(data[data.length - 1].id);
            }

            setHasMore(data.length === 10);

        } else {
            console.log("error")
            alert(response.message);
        }

        setLoading(false);
    };

    const refreshBreeds = async () => {

        const response = await ApiService.get(BREEDS_ENDPOINT + `?lastId=0`)

        if (response.success) {

            let data = response.data
            setBreeds(data);

            if (data.length > 0) {
                setLastId(data[data.length - 1].id);
            }

            setHasMore(data.length === 10);
        } else {
            alert(response.message);
        }
    }

    useEffect(() => {
        fetchBreeds();
    }, []);


    return (<>
        <div className="breed-page">
            <div className='header-div'>
                <h2>Dog Breeds 🐶</h2>
                <div className='header-actions'>
                    <button onClick={() => setShowAddPopUp(true)}>Add breed</button>
                    <button onClick={() => setShowImportPopUp(true)}>Import breeds</button>
                </div>
            </div>

            <table className="breed-table">
                <tr>
                    <th>Name</th>
                    <th>Number of sub breeds</th>
                    <th>Last modified at</th>
                    <th>Action</th>
                </tr>
                {
                    loading ? (
                        <tr>
                            <td colSpan="4" className="no-breeds-container">
                                Loading breeds...
                            </td>
                        </tr>
                    ) : breeds.length !== 0 ? breeds.map((breed, index) => {
                        return (
                            <tr
                                key={index}
                            >
                                <td><span className="breed-name" onClick={() => navigate(`/breeds/${breed.id}`, {
                                    state: { breedName: breed.name }
                                })}>{breed.name}</span></td>
                                <td>{breed.subBreeds?.length}</td>
                                <td>
                                    {new Date(breed.modifiedAt).toLocaleString('en-US', {
                                        timeZone: 'Asia/Kolkata',
                                        month: 'short',
                                        day: 'numeric',
                                        hour: 'numeric',
                                        minute: '2-digit'
                                    })}
                                </td>
                                <td>
                                    <button className="edit-button"
                                        title="edit breed"
                                        onClick={() => {
                                            setSelectedBreed(breed);
                                            setShowEditPopUp(true);
                                        }}>✏️</button>
                                    <button className="delete-button"
                                        title="delete breed"
                                        onClick={() => {
                                            setSelectedBreed(breed);
                                            setShowConfirmDeletePopUp(true);
                                        }}>🗑️</button>
                                </td>
                            </tr>
                        );
                    }) : <tr>
                        <td colSpan='4' className='no-breeds-container'>
                            No breeds yet
                        </td></tr>
                }
            </table>

            {hasMore && (<div className='cursor-page-load-more'>
                <button onClick={fetchBreeds}>
                    Load More
                </button></div>
            )}

            {
                showEditPopUp && selectedBreed && <EditBreedForm BREEDS_ENDPOINT={BREEDS_ENDPOINT} setShowEditPopUp={setShowEditPopUp} selectedBreed={selectedBreed} setBreeds={setBreeds} />
            }

            {
                showConfirmDeletePopUp && selectedBreed && <DeleteBreedPopup BREEDS_ENDPOINT={BREEDS_ENDPOINT} setShowConfirmDeletePopUp={setShowConfirmDeletePopUp} selectedBreed={selectedBreed} setBreeds={setBreeds} />
            }

            {
                showAddPopUp && <AddBreedForm BREEDS_ENDPOINT={BREEDS_ENDPOINT} hasMore={hasMore} setShowAddPopUp={setShowAddPopUp} breeds={breeds} setBreeds={setBreeds} />
            }

            {
                showImportPopUp && <ImportFilePopup BREEDS_ENDPOINT={BREEDS_ENDPOINT} refreshBreeds={refreshBreeds} setShowImportPopUp={setShowImportPopUp} breeds={breeds} setBreeds={setBreeds} />
            }
        </div>
    </>)
}