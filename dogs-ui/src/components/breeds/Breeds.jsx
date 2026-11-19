import { useState, useEffect } from 'react';
import '../../styles/breeds.css';
import { useNavigate } from 'react-router';
import AddBreedForm from './forms/AddBreedForm';
import EditBreedForm from './forms/EditBreedForm';
import DeleteBreedPopup from './actions/DeleteBreedPopup';
import ImportFilePopup from './actions/ImportFilePopup';

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

    const BREEDS_API_URL = import.meta.env.VITE_DOGS_API_URL + "/v1/breeds";


    const fetchBreeds = async () => {

        console.log(BREEDS_API_URL, lastId);

        const response = await fetch(
            BREEDS_API_URL + `?lastId=${lastId}`
        );

        const data = await response.json();
        console.log("Breeds : ", data)

        setBreeds(prev => [...prev, ...data]);

        if (data.length > 0) {
            setLastId(data[data.length - 1].id);
        }

        if (data.length < 10) {
            setHasMore(false);
        } else {
            setHasMore(true);
        }
    };

    const refreshBreeds = async () => {

        const response = await fetch(
            BREEDS_API_URL + `?lastId=0`
        );

        const data = await response.json();
        setBreeds(data);

        if (data.length > 0) {
            setLastId(data[data.length - 1].id);
        } else {
            setLastId(0);
        }

        setHasMore(data.length === 10);
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
                    breeds.length !== 0 ? breeds.map((breed, index) => {
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
                showEditPopUp && selectedBreed && <EditBreedForm BREEDS_API_URL={BREEDS_API_URL} setShowEditPopUp={setShowEditPopUp} selectedBreed={selectedBreed} setBreeds={setBreeds} />
            }

            {
                showConfirmDeletePopUp && selectedBreed && <DeleteBreedPopup BREEDS_API_URL={BREEDS_API_URL} setShowConfirmDeletePopUp={setShowConfirmDeletePopUp} selectedBreed={selectedBreed} setBreeds={setBreeds} />
            }

            {
                showAddPopUp && <AddBreedForm BREEDS_API_URL={BREEDS_API_URL} hasMore={hasMore} setShowAddPopUp={setShowAddPopUp} breeds={breeds} setBreeds={setBreeds} />
            }

            {
                showImportPopUp && <ImportFilePopup BREEDS_API_URL={BREEDS_API_URL} refreshBreeds={refreshBreeds} setShowImportPopUp={setShowImportPopUp} breeds={breeds} setBreeds={setBreeds} />
            }
        </div>
    </>)
}