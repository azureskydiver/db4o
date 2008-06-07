using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using OManager.BusinessLayer.Common;
using OME.Logging.Common;

namespace OMControlLibrary.Common
{
    class Validations
    {

        public static bool ValidateDataType(string dataType,ref object data)
        {
            bool isValidated = false;
            try
            {
                switch (dataType)
                {

                    case OManager.BusinessLayer.Common.BusinessConstants.SINGLE:
                        if (data != null)
                            isValidated = ValidateSingle(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.DATETIME:
                        if (data != null)
                            isValidated = ValidateDateTime(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.BYTE:
                        if (data != null)
                            isValidated = ValidateBYTE(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.CHAR:
                        if (data != null)
                            isValidated = ValidateCharacter(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.BOOLEAN:
                        if (data != null)
                            isValidated = ValidateBool(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.DECIMAL:
                        if (data != null)
                            isValidated = ValidateDecimal(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.DOUBLE:
                        if (data != null)
                            isValidated = ValidateDouble(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.INT16:
                        if (data != null)
                            isValidated = ValidateInt16(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.INT32:
                        if (data != null)
                            isValidated = ValidateInt(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.INT64:
                        if (data != null)
                            isValidated = ValidateInt64(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.SBYTE:
                        if (data != null)
                            isValidated = ValidateSBYTE(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.UINT16:
                        if (data != null)
                            isValidated = ValidateUINT16(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.UINT32:
                        if (data != null)
                            isValidated = ValidateUINT32(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.UINT64:
                        if (data != null)
                            isValidated = ValidateUINT64(data);
                        break;
                    case OManager.BusinessLayer.Common.BusinessConstants.STRING:
                        if (data == null)
                            data = string.Empty;
                        isValidated = true;
                        break;
                    default:
                        isValidated = false;
                        break;
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);   
            }
            return isValidated;
        }


        public static bool  ValidateBool(object obj)
        {
             bool isValidated = true;
           
            try
            {

                bool checkBool;
                checkBool = Convert.ToBoolean(obj);
            }
            catch (Exception)
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                                Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                                MessageBoxButtons.OK, 
                                MessageBoxIcon.Exclamation);
                isValidated = false ;
            }
            return isValidated;
           
        }
        public static bool  ValidateInt(object obj)
        {
            bool isValidated = true;
            try
            {

                Int32  checkInt32;
                checkInt32 = Convert.ToInt32(obj);
            }
            catch (Exception)
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool ValidateInt16(object obj)
        {
            bool isValidated = true;
            try
            {

                Int16 checkInt16;
                checkInt16 = Convert.ToInt16(obj);
            }
            catch (Exception)
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }

        public static bool ValidateInt64(object obj)
        {
            bool isValidated = true;
            try
            {
                Int64 checkInt64;
                checkInt64 = Convert.ToInt64(obj);
            }
            catch (Exception)
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool ValidateUINT32(object obj)
        {
            bool isValidated = true;
            try
            {

                UInt32 checkUInt32;
                checkUInt32 = Convert.ToUInt32(obj);
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool ValidateUINT16(object obj)
        {
            bool isValidated = true;
            try
            {

                UInt16 checkInt16;
                checkInt16 = Convert.ToUInt16(obj);
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool ValidateUINT64(object obj)
        {
            bool isValidated = true;
            try
            {

                UInt64 checkInt64;
                checkInt64 = Convert.ToUInt64(obj);
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool ValidateSBYTE(object obj)
        {
            bool isValidated = true;
            try
            {

               sbyte sByte;
               sByte = Convert.ToSByte(obj);
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool ValidateBYTE(object obj)
        {
            bool isValidated = true;
            try
            {

                byte Byte;
                Byte = Convert.ToByte(obj);
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool  ValidateDouble(object obj)
        {
            bool isValidated = true;
            try
            {

                double checkDouble;
                checkDouble = Convert.ToDouble(obj); 
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool ValidateDecimal(object obj)
        {
            bool isValidated = true;
            try
            {

                decimal checkDecimal;
                checkDecimal = Convert.ToDecimal(obj);
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }
        public static bool ValidateDateTime(object obj)
        {
            bool isValidated = true;
            try
            {

                DateTime  checkDate;
                checkDate = Convert.ToDateTime(obj);
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
                
            }
            return isValidated;
        }

        public static bool ValidateSingle(object obj)
        {
            bool isValidated = true;
            try
            {

                Single  checkSingle;
                checkSingle = Convert.ToSingle(obj);
            }
            catch (Exception  )
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
               isValidated = false ;

            }
            return isValidated;
        }
        public static bool ValidateCharacter(object obj)
        {
            bool isValidated = true;
            try
            {

                char  checkChar;
                checkChar = Convert.ToChar(obj);
            }
            catch (Exception)
            {
                MessageBox.Show(Helper.GetResourceString(Constants.VALIDATION_MSG_INVALIDE_VALUE),
                              Helper.GetResourceString(Constants.PRODUCT_CAPTION),
                              MessageBoxButtons.OK,
                              MessageBoxIcon.Exclamation);
                isValidated = false;
            }
            return isValidated;
        }

        public static bool ValidateRemoteLoginParams(ref ToolTipComboBox comboBoxFilePath,  ref TextBox textBoxHost, ref TextBox textBoxPort, ref TextBox textBoxUserName, ref TextBox textBoxPassword)
        {
            try
            {
                if (comboBoxFilePath.Text.Trim().Equals(Helper.GetResourceString(Common.Constants.COMBOBOX_DEFAULT_TEXT))
                            && textBoxHost.Text.Trim().Equals(string.Empty)
                            && textBoxPort.Text.Trim().Equals(string.Empty)
                            && textBoxUserName.Text.Trim().Equals(string.Empty)
                            && textBoxPassword.Text.Trim().Equals(string.Empty))
                {
                    MessageBox.Show(Helper.GetResourceString(Common.Constants.VALIDATION_MSG_SELECT_REMOTE_CONNECTION), 
                        Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                    comboBoxFilePath.Focus();
                    return false;
                }
                if (textBoxHost.Text.Trim().Equals(string.Empty))
                {
                    MessageBox.Show(Helper.GetResourceString(Common.Constants.VALIDATION_MSG_ENTER_HOST), 
                        Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION),
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                    textBoxHost.Focus();
                    return false;
                }
                if (textBoxPort.Text.Trim().Equals(string.Empty))
                {
                    MessageBox.Show(Helper.GetResourceString(Common.Constants.VALIDATION_MSG_ENTER_PORT),
                        Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                    textBoxPort.Focus();
                    return false;
                }
                if (textBoxUserName.Text.Trim().Equals(string.Empty))
                {
                    MessageBox.Show(Helper.GetResourceString(Common.Constants.VALIDATION_MSG_ENTER_USERNAME), 
                        Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                    textBoxUserName.Focus();
                    return false;
                }
                if (textBoxPassword.Text.Trim().Equals(string.Empty))
                {
                    MessageBox.Show(Helper.GetResourceString(Common.Constants.VALIDATION_MSG_ENTER_PASSWORD), 
                        Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                    textBoxPassword.Focus();
                    return false;
                }

                if (!(Convert.ToInt32(textBoxPort.Text.Trim()) >= 1 && Convert.ToInt32(textBoxPort.Text.Trim()) <= 65535))
                { 
                    MessageBox.Show(Helper.GetResourceString(Common.Constants.VALIDATION_MSG_PORT_RANG), 
                        Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION), 
                        MessageBoxButtons.OK, MessageBoxIcon.Information);
                    textBoxPort.Focus();
                    return false;
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
            return true;
        }

        public static bool ValidateLocalLoginParams(ref ToolTipComboBox comboBoxFilePath, ref TextBox textBoxConnection)
        {
            try
            {
                if ((comboBoxFilePath.Text.Trim().Equals(Helper.GetResourceString(Common.Constants.COMBOBOX_DEFAULT_TEXT)) && textBoxConnection.Text.Trim().Equals(string.Empty)) || ((comboBoxFilePath.Text.Trim().Equals(string.Empty) && textBoxConnection.Text.Trim().Equals(string.Empty))))
                {
                    MessageBox.Show(Helper.GetResourceString(Common.Constants.VALIDATION_MSG_SELECT_DATABASE), Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.OK, MessageBoxIcon.Information);
                    comboBoxFilePath.Focus();
                    return false;
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.HandleException(oEx);
                return false;
            }
            return true;

        }


    }
}
