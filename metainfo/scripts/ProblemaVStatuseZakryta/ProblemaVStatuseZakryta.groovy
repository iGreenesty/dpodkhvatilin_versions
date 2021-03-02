def STATES = ['closed', 'solved']
return (subject.state in STATES) ? '' : 'Проблема не закрыта и не решена'