def DECLINE_STATES = ['closed', 'solved']
return (subject.state in DECLINE_STATES) ? 'Проблема закрыта или решена' : ''