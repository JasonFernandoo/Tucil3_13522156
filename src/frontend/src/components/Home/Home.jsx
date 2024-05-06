import React from 'react';
import './Home.css';

const Home = () => {
  return (
    <div className='home'>
      <div className='loader'>
        <div className='loader-clip clip-top'>
          <div className='marquee'>
            <div className='marquee-container'>
              <span>Word</span>
              <span>Word</span>
              Word
              <span>Word</span>
              <span>Word</span>
            </div>
          </div>
        </div>
        <div className='loader-clip clip-bottom'>
          <div className='marquee'>
            <div className='marquee-container'>
              <span>Ladder</span>
              <span>Ladder</span>
              Ladder
              <span>Ladder</span>
              <span>Ladder</span>
            </div>
          </div>
        </div>
        <div className='clip-center'>
          <div className='marquee'>
            <div className='marquee-container center'>
              <span>Word</span>
      
              Word Ladder
              
              <span>Ladder</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;