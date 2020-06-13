import React, {Component} from "react";
import {Link} from "react-router-dom";

class DiscountList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            discounts: [],
        }
    }

    componentDidMount() {
        let url = "http://localhost:9000/api/discounts"

        fetch(url, {
            mode: 'cors',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': 'http://localhost:3000',
            },
            method: 'GET',
        })
            .then(results => {
                return results.json();
            }).then(data => {
            let discounts = data.map((discount) => {
                return (
                    <li key={discount.id}>
                        <Link to={"/discount/" + discount.id}>{discount.code} - {discount.value}% off</Link>
                    </li>
                )
            })
            this.setState({discounts: discounts})
        })
    }

    render() {
        return (
            <div className="container">
                <div className="discounts">
                    <h3>List of all discount codes</h3>
                    <ul>
                        {this.state.discounts}
                    </ul>
                </div>
                <Link to="/discount/add" className="btn btn-md btn-success">Add new discount code</Link>
            </div>
        )
    }
}

export default DiscountList;