def DONE_STATES = ['resolved', 'closed']
return (subject.state in DONE_STATES) ? 'Задача выполнена' : ''