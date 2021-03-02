//ПАРАМЕТРЫ------------------------------------------------------------

// код заполняемого атрибута
ATTR_CODE = "resumptionSum";
ATTR_VALUE = 0;


//ОСНОВНОЙ БЛОК--------------------------------------------------------
if (subject[ATTR_CODE] > (0)) {
resumptionSum = subject[ATTR_CODE];
} else {
resumptionSum = 0
}
ATTR_VALUE = resumptionSum + 1;
utils.edit(subject, [(ATTR_CODE) : ATTR_VALUE]);