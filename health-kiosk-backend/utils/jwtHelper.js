var jwt = require('jsonwebtoken')

const JWT_SECRET = process.env.JWT_SECRET;

function generateToken(data){
    const token = jwt.sign({data},JWT_SECRET,{expiresIn: '60min'});
    return token;
}

function verifyToken(data){
    try{
        jwt.verify(data,JWT_SECRET)
    }catch(error){
        throw new Error(error.message||'Invalid token')
    }
}

module.exports = {generateToken,verifyToken}