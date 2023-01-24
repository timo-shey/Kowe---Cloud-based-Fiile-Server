import React from "react";
import "./BudgetCategoryList.css";

const BudgetCategoryCard = ({ item }) => {
  return (
      <div className="frame-8722-XPf budgetCategoryList">
        <div className="frame-8717-FaZ">
          <p className="transportation-YJm">{item.name}</p>
        </div>
        <img className="option-sM3" src="/assets/option-uxu.png" />
    </div>
  );
};

export default BudgetCategoryCard;
