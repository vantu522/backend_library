from flask import Flask, request, jsonify
from werkzeug.utils import secure_filename
import os

app = Flask(__name__)

@app.route('/api/addBook', methods=['POST'])
def add_book():
    name = request.form.get('name')
    image = request.files.get('image')
    
    if image:
        filename = secure_filename(image.filename)
        image.save(os.path.join('uploads', filename))  # Lưu hình ảnh

    book = {
        'name': name,
        'image_filename': filename if image else None
    }

    return jsonify(book)

if __name__ == '__main__':
    app.run(debug=True)
