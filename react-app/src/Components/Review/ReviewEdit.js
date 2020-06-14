import React, {Component} from "react";
import {Redirect} from "react-router-dom";
import InputComponent from "../Common/InputComponent";

class ReviewEdit extends Component {
    constructor(props) {
        super(props);
        this.state = {
            id: props.match.params.id,
            productId: null,
            rate: 0,
            comment: 0,
            reviewCreated: false
        }
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    handleInputChange(name, value) {
        this.setState({
            [name]: value
        });
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/review/" + this.state.id;
        fetch(url, {
            mode: 'cors',
            headers: {},
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            this.setState({
                id: data.review.id,
                productId: data.review.product,
                rate: data.review.rate,
                comment: data.review.comment,
            })
            let inputs = (
                <div>
                    <InputComponent label="rate" name="rate" value={this.state.rate}
                                    onChangeCallback={this.handleInputChange}/>
                    <InputComponent label="comment" name="comment" value={this.state.comment}
                                    onChangeCallback={this.handleInputChange}/>
                </div>
            )
            this.setState({inputs: inputs})
        });
    }

    handleSubmit(event) {
        event.preventDefault();
        var data = {
            "id": this.state.id,
            "product": parseInt(this.state.productId),
            "rate": parseInt(this.state.rate),
            "comment": this.state.comment
        };
        var url = 'http://localhost:9000/api/review/' + this.state.id;

        fetch(url, {
            method: 'PUT',
            headers: {},
            body: JSON.stringify(data),
        }).then(results => {
            return results.json();
        }).then(responseData => {
            this.setState({
                reviewCreated: true,
                id: responseData.review.id
            })
        });
    }

    render() {
        if (this.state.reviewCreated) {
            return <Redirect to={"/product/" + this.state.productId}/>
        }
        return (
            <div>
                {this.state.inputs}
                <button className="btn btn-sm btn-success" onClick={this.handleSubmit}>Edit review</button>
            </div>
        );
    }
}

export default ReviewEdit;