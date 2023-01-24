import React from "react";
import "./BudgetCategoryList.css";
import BudgetCategoryCard from "./BudgetCategoryCard";
import { useState, useEffect } from "react";
import {baseEndpoint} from "../../../globalresources/Config";

function BudgetCategoryList() {

  const [list, setList] = useState([]);

  useEffect(() => {
    const getBudgetCategoryList = async() => {
      const taskFromServer = await fetchBudgetCategories();
      setList(taskFromServer);
    }

    getBudgetCategoryList()
  }, []);

  const fetchBudgetCategories = async ()=> {
    const token = localStorage.getItem("token");

    const res = await fetch(baseEndpoint+'/api/v1/budgets/category', {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${token}`,
        }
      });
  
      const data = await res.json();
      return data;
  }

  

  return (
    <div className="budget-category-list-decapay-sY5">
      <img className="ellipse-4-18V" src="/assets/ellipse-4-hKF.png" />
      <div className="frame-8797-XMj">
        <div className="frame-8780-TWH">
          <img className="back-arrow-aqo" src="/assets/back-arrow-mRT.png" />
          <p className="back-Xm3">Back</p>
        </div>

        <div className="frame-8796-dZB">
          <div className="frame-8795-Bqb">
            {list.length > 0 ? list.map((item) => (<BudgetCategoryCard key={item.id} item={item} />)) : ""}
          </div>

          <div className="frame-8754-bER">
            <img className="plus-iZw" src="/assets/plus-zeD.png" />
            <div className="create-budget-TnR">Create Budget</div>
          </div>
        </div>

      </div>
    </div>
  );
}

export default BudgetCategoryList;
