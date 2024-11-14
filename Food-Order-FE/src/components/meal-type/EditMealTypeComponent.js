import React, { useState, useEffect } from 'react';
import { Form, Button } from 'react-bootstrap';
import MealTypeService from '../../services/MealTypeService';

const EditMealTypeComponent = (props) => {
    const mealType = props.mealType;
    const selectedFile = props.file;
    const [typeName, setTypeName] = useState(mealType.typeName || '');
    const [description, setDescription] = useState(mealType.description || '');

    useEffect(() => {
        setTypeName(mealType.typeName || '');
        setDescription(mealType.description || '');
    }, [mealType]);

    const onChoseFile = (e) => {
        selectedFile.setSelectedFile(e.target.files[0]);
        console.log("Selected file:", e.target.files[0]);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('mealType', JSON.stringify({ ...mealType, typeName, description }));

        if (selectedFile.selectedFile) {
            formData.append('image', selectedFile.selectedFile);
        }

        try {
            const response = await MealTypeService.updateMealType(formData);
            if (response.data === 'success') {
                alert('Meal Type updated successfully!');
                // Gọi lại hàm để cập nhật dữ liệu trong ứng dụng chính
                if (props.onUpdateSuccess) props.onUpdateSuccess();
            } else {
                alert('Failed to update Meal Type. Please try again.');
            }
        } catch (error) {
            console.error('Error updating Meal Type:', error);
            alert('An error occurred while updating the Meal Type.');
        }
    };

    return (
        <div>
            <div className='container-add-meal'>
                <form onSubmit={handleSubmit}>
                    <div className='form-group mb-2'>
                        <label className='form-label'>Type Name: </label>
                        <input
                            type="text"
                            placeholder="Insert name"
                            name="typeName"
                            className="form-control"
                            value={typeName}
                            onChange={(e) => setTypeName(e.target.value)}
                        />
                    </div>

                    <div className='form-group mb-2'>
                        <label className='form-label'>Description: </label>
                        <input
                            type="text"
                            placeholder="Insert description"
                            name="description"
                            className="form-control"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                        />
                    </div>

                    <div className='form-group mb-2'>
                        <label className='form-label'>Upload Image </label>
                        <input
                            type="file"
                            name="image"
                            className="form-control"
                            onChange={onChoseFile}
                        />
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditMealTypeComponent;
