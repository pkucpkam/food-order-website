// Comment.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // Import useNavigate for routing
import CommentService from '../../services/CommentService';
import './Comment.css';

const Comment = ({ mealId }) => {
    const [comments, setComments] = useState([]);
    const [newComment, setNewComment] = useState('');
    
    const currentUser = sessionStorage.getItem('username') || 'Guest';
    const navigate = useNavigate(); // Initialize navigate for routing

    useEffect(() => {
        if (currentUser === 'Guest') {
            navigate('/login');
        } else {
            CommentService.getComments(mealId)
                .then(response => {
                    console.log("Data received from API:", response.data); 
                    setComments(response.data);
                })
                .catch(error => console.error("Error fetching comments:", error));
        }
    }, [mealId, currentUser, navigate]);

    // Handle submitting a new comment
    const handleCommentSubmit = (e) => {
        e.preventDefault();
        if (newComment.trim() !== '') {
            const newCommentObject = {
                id: null, 
                author: currentUser,
                content: newComment,
                created_at: new Date().toISOString(), 
                mealId: mealId,
            };

            console.log(newCommentObject)
    
            CommentService.addComment(newCommentObject)
                .then(response => {
                    setComments([...comments, response.data]);
                    setNewComment('');
                })
                .catch(error => console.error("Error adding comment:", error));
        }
    };

    const handleDeleteComment = (commentId) => {
        // Call delete comment API
        CommentService.deleteComment(commentId)
            .then(() => {
                setComments(comments.filter(comment => comment.id !== commentId));
            })
            .catch(error => console.error("Error deleting comment:", error));
    };

    return (
        <div className="comments-section">
            <h3>Comments</h3>
            <form onSubmit={handleCommentSubmit}>
                <textarea
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    placeholder="Add a comment"
                />
                <button type="submit">Submit</button>
            </form>
            <ul className="comments-list">
                {comments.map((comment) => (
                    <li key={comment.id} className="comment-item">
                        <div className="comment-content">
                            <strong>{comment.author}</strong> - <em>{comment.created_at || "Unknown date"}</em>
                            <p>{comment.content}</p>
                        </div>
                        {comment.author === currentUser && (
                            <button
                                className="delete-comment-btn"
                                onClick={() => handleDeleteComment(comment.id)}
                            >
                                Delete
                            </button>
                        )}
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default Comment;
