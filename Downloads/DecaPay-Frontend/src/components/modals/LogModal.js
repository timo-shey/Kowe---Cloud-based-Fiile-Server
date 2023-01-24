import styles from './LogModal.module.css';
import { useState } from 'react';
import BigCard from '../../UI/BigCard';
import Button from '../../UI/Button';
import cancel from '../../UI/icons/close-cKT.png'


const LogModal = () => {
    const[logModal, setLogModal]= useState(true)

    const[form, setForm] = useState({
        amountSpent:"",
        description:""
    })

    const handleChange = (event)=>{
            const {value, name} = event.target;
            setForm(prevForm=>({
                ...prevForm,
                [name]:value
             } )
            )   
    }

    const handleClick = ()=>{
        
    }

    const imageClick = ()=>{
        setLogModal(null)
    }

    return ( 
        <>

        { logModal &&
        <div>
             <div className={styles.backdrop}  onClick={imageClick}></div>
     <BigCard className={styles.logmodal}>
            
            
            <img src={cancel} alt="cancel icon " 
            style={{ 
                    width: '30px',
                    height: '30px',
                    position:"relative",
                    left:'452px',
                    top: '40px'
                    }}
                    onClick={imageClick}/>
    
        <div>
        <p className={styles.title}>Log Item</p>
        </div>
        <form>
        <label  className= {styles.label} htmlFor='amountspent'>Amount Spent</label> <br />
                <div className={styles.amount}>         
                    <input 
                       id='amountspent'
                        type="text"
                        name= "amountspent" 
                        onChange={handleChange}
                        value={form.amountSpent}
                        placeholder='N0.00'
                     />
                </div>
            
         <label  className= {styles.label} htmlFor='description'>Description</label> <br />
                <div className={styles.amount}>         
                    <input 
                       id='description'
                        type="text"
                        name= "description" 
                        onChange={handleChange}
                        value={form.description}
                        placeholder='Type description here'
                     />
                </div>
                <br />
                <button className= {styles.btntwo} onClick={handleClick} >Save</button>
                <button className={styles.btns} onClick={imageClick} >Cancel</button>
        </form>
     </BigCard>
        </div>
       
       }
        
        </>       
     );
}
 
export default LogModal;