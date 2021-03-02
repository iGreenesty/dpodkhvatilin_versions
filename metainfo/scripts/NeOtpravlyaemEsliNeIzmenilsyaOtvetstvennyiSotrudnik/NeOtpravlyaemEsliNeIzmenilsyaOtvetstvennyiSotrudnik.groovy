def nowResp = subject.responsibleEmployee
def oldResp = oldSubject?.responsibleEmployee
return (nowResp && nowResp?.UUID == oldResp?.UUID) ? 'Ответственный сотрудник не изменился' : ""