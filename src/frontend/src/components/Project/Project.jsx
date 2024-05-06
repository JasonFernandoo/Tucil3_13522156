import React, { useState, useEffect } from 'react';
import './Project.css';

const Project = () => {
  const [selectedButton, setSelectedButton] = useState('');
  const [firstText, setFirstText] = useState('');
  const [secondText, setSecondText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [executionTime, setExecutionTime] = useState(0);
  const [path, setPath] = useState(null);
  const [rotationDegree, setRotationDegree] = useState('0deg');
  const [exception, setException] = useState(null);
  const [visitedWords, setVisitedWords] = useState(0);
  const [memoryUsage, setMemoryUsage] = useState(null);

  const handleSubmit = async (event) => {
    setPath(null);
    setException(null);
    event.preventDefault();
    setIsLoading(true);
    document.querySelector('.project-card').style.perspective = 'none';
    const startTime = performance.now();

    const isValidFirstText = await isValidEnglishWord(firstText);
    const isValidSecondText = await isValidEnglishWord(secondText);

    if (!isValidFirstText || !isValidSecondText) {
      console.error('One or both of the input words are not valid English words');
      setException('One or both of the input words are not valid English words');
      return;
    }

    console.log(`First text: ${firstText}`);
    console.log(`Second text: ${secondText}`);
    console.log(`Selected algorithm: ${selectedButton}`);
    const initialMemoryUsage = window.performance && window.performance.memory ? window.performance.memory.usedJSHeapSize : null;

  fetch(`http://localhost:8000/findLadder?startWord=${firstText}&endWord=${secondText}&algorithm=${selectedButton}`)
  .then(response => response.text())  
  .then(text => {
    console.log(text); 
    return JSON.parse(text);  
  })
  .then(data => {
    setPath(data.path);
    setVisitedWords(data.totalNodesVisited);

    const finalMemoryUsage = window.performance && window.performance.memory ? window.performance.memory.usedJSHeapSize : null;

    if (initialMemoryUsage !== null && finalMemoryUsage !== null) {
      const memoryUsed = finalMemoryUsage - initialMemoryUsage;
      setMemoryUsage(memoryUsed / 1024); 
    }
  })
  .catch(error => {
    console.error('No Path Found');
    setException('No Path Found');
  });
    

    const endTime = performance .now(); 
    const executionTime = (endTime - startTime) / 1000; 
    setExecutionTime(executionTime);
    console.log(`Execution time: ${executionTime} s`);
  };
  

  const handleClick = (algorithm) => {
    setSelectedButton(algorithm);
  };

  const isTransformed = (char1, char2) => {
    return char1 !== char2;
  };

  const isValidEnglishWord = async (word) => {
    try {
      const response = await fetch(`https://api.dictionaryapi.dev/api/v2/entries/en/${word}`);
      if (!response.ok) {
        throw new Error('Word not found in the dictionary');
      }
      const data = await response.json();
      return data.length > 0;
    } catch (error) {
      console.error('There was a problem with the fetch operation:', error);
      return false;
    }
  };

  const [titleFontSize, setTitleFontSize] = useState('100px');

  useEffect(() => {
    const handleScroll = () => {
      const scrollPercentage = (window.scrollY / (document.documentElement.scrollHeight - window.innerHeight)) * 200;
      const newSize = 200 - scrollPercentage * 0.6;
      setTitleFontSize(`${newSize}px`);

      document.querySelector('.project-card').style.perspective = '1000px';
      const newRotationDegree = 90 - scrollPercentage * 0.45;
      setRotationDegree(`${newRotationDegree}deg`);
    };

    window.addEventListener('scroll', handleScroll);

    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);

  return (
    <div className='project'>
      <div className='project-title'>
        <p style={{ fontSize: titleFontSize }}>Word Ladder</p>
      </div>
      <div className={`project-card ${isLoading ? 'is-flipped' : ''}`}>
        <div className='project-container-wrapper' style={{ transform: `rotateX(${rotationDegree})` }}>
          <div className='project-container'>
            <form>
              <div className='project-left'>
                <p className='left-title'>Input Here</p>
                <div className='left-input'>
                  <div className='input-group'>
                    <input type='text' required className='input' value={firstText} onChange={e => setFirstText(e.target.value)}/>
                    <label className='user-label'>First Text</label>
                  </div>
                  <p>to</p>
                  <div className='input-group'>
                    <input type='text' required className='input' value={secondText} onChange={e => setSecondText(e.target.value)}/>
                    <label className='user-label'>Second Text</label>
                  </div>
                </div>
              </div>
              <div className='project-right'>
                <p className='right-title'>Choose Algorithm</p>
                <div className='right-top'>
                  <button type='button' value="UCS" className={selectedButton === 'UCS' ? 'selected' : ''} onClick={() => handleClick('UCS')}>UCS</button>
                  <button type='button' value="GBFS" className={selectedButton === 'GBFS' ? 'selected' : ''} onClick={() => handleClick('GBFS')}>GBFS</button>
                  <button type='button' value="ASTAR" className={selectedButton === 'ASTAR' ? 'selected' : ''} onClick={() => handleClick('ASTAR')}>A*</button>
                </div>
                <div className='right-bottom'>
                  <button onClick={handleSubmit} type='button' disabled={isLoading}>
                    {isLoading ? 'Loading...' : 'Submit'}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
        <div className='project-result'>
          <div className='project-result-exit'>
            <button onClick={() => {setIsLoading(false);setFirstText('');setSecondText('');setSelectedButton('');document.querySelector('.project-card').style.perspective = 'none';}}>X</button>          
          </div> 
          <div className='result-text'> 
            <div className='result-algorithm'>
              <p>{selectedButton}</p>
            </div>
            <div className='result-input-first'>
              {firstText.split('').map((char, index) => (
                <div key={index} className='char-box'>
                  {char.toUpperCase()}
                </div>
              ))}
            </div>
            <div className='result-path'>
              {exception ? (
                <p className='exception'>{exception}</p>
              ) : (
                path && path.map((char, index) => (
                  <div key={index} className='char-box-path'>
                    {char && char.split('').map((c, i) => (
                      <div key={i} className={isTransformed(c, secondText[i]) ? 'char-box' : 'char-box-transformed'}>
                        {c.toUpperCase()}
                      </div>
                    ))}
                  </div>
                ))
              )}
            </div>
            <div className='result-input-second'>
              {secondText.split('').map((char, index) => (
                <div key={index} className='char-box-end'>
                  {char.toUpperCase()}
                </div>
              ))}
            </div>
            <div className='result-cost'>
              <p>Time: {executionTime.toFixed(4)} Seconds</p>
              <p>Node: {path? path.length : 0}</p>
              <p>Visited: {visitedWords}</p>
              <p>Memory: {memoryUsage.toFixed(2)} KB</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Project;