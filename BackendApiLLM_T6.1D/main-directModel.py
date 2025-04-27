import os
from flask import Flask, request, jsonify
import re
from transformers import AutoTokenizer, AutoModelForCausalLM
import torch

app = Flask(__name__)

# To load AI model and text processor
# MODEL = "meta-llama/Llama-3.2-1B"
MODEL = "google/gemma-3-1b-it"
tokenizer = AutoTokenizer.from_pretrained(MODEL)
model = AutoModelForCausalLM.from_pretrained(MODEL)

# To check if we can use GPU or need CPU
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
model.to(device)  # To put model on correct hardware
print(f"Using device: {device}")

def fetchQuizFromLlama(student_topic):
    print(f"Generating quiz for topic: {student_topic} using {MODEL}")
    # To create instruction for AI
    prompt = (
        f"Generate a quiz with 3 questions to test students on the provided topic. "
        f"For each question, generate 4 options where only one of the options is correct. "
        f"Format your response as follows:\n"
        f"**QUESTION 1:** [Your question here]?\n"
        f"**OPTION A:** [First option]\n"
        f"**OPTION B:** [Second option]\n"
        f"**OPTION C:** [Third option]\n"
        f"**OPTION D:** [Fourth option]\n"
        f"**ANS:** [Correct answer letter]\n\n"
        f"**QUESTION 2:** [Your question here]?\n"
        f"**OPTION A:** [First option]\n"
        f"**OPTION B:** [Second option]\n"
        f"**OPTION C:** [Third option]\n"
        f"**OPTION D:** [Fourth option]\n"
        f"**ANS:** [Correct answer letter]\n\n"
        f"**QUESTION 3:** [Your question here]?\n"
        f"**OPTION A:** [First option]\n"
        f"**OPTION B:** [Second option]\n"
        f"**OPTION C:** [Third option]\n"
        f"**OPTION D:** [Fourth option]\n"
        f"**ANS:** [Correct answer letter]\n\n"
        f"Ensure text is properly formatted. It needs to start with a question, then the options, and finally the correct answer. "
        f"Follow this pattern for all questions. "
        f"Here is the student topic:\n{student_topic}"
    )

    try:
        # To convert text to numbers AI can understand
        inputs = tokenizer(prompt, return_tensors="pt").to(device)  # To put on same device as model

        # To make AI create quiz text
        outputs = model.generate(
            **inputs,
            max_new_tokens=500,  # To limit how long answer can be
            temperature=0.7,     # To control randomness
            top_p=0.9,           # To focus on likely words
            do_sample=True,      # To make answers more creative
            pad_token_id=tokenizer.eos_token_id  # To avoid errors with text length
        )

        # To convert AI numbers back to text
        generated_text = tokenizer.decode(outputs[0], skip_special_tokens=True)

        # To find where quiz starts in text
        quiz_start = generated_text.find("**QUESTION 1:**")
        if quiz_start == -1:
            raise Exception("Failed to generate a properly formatted quiz")
        quiz_text = generated_text[quiz_start:]
        return quiz_text
    except Exception as e:
        raise Exception(f"Failed to generate quiz with model: {str(e)}")

def process_quiz(quiz_text):
    questions = []
    # To find all questions in AI response
    pattern = re.compile(
        r'\*\*QUESTION \d+:\*\* (.+?)\n'
        r'\*\*OPTION A:\*\* (.+?)\n'
        r'\*\*OPTION B:\*\* (.+?)\n'
        r'\*\*OPTION C:\*\* (.+?)\n'
        r'\*\*OPTION D:\*\* (.+?)\n'
        r'\*\*ANS:\*\* (.+?)(?=\n|$)',
        re.DOTALL
    )
    matches = pattern.findall(quiz_text)

    # To create question objects from text
    for match in matches:
        question = match[0].strip()
        options = [match[1].strip(), match[2].strip(), match[3].strip(), match[4].strip()]
        correct_ans = match[5].strip()

        # To make data structure for each question
        question_data = {
            "question": question,
            "options": options,
            "correct_answer": correct_ans
        }
        questions.append(question_data)

    return questions

@app.route('/getQuiz', methods=['GET'])
def get_quiz():
    print("Request received")
    # To get topic from app request
    student_topic = request.args.get('topic')
    if not student_topic:
        return jsonify({'error': 'Missing topic parameter'}), 400
    try:
        # To generate quiz using AI
        quiz = fetchQuizFromLlama(student_topic)
        print(quiz)
        # To convert AI text to quiz format
        processed_quiz = process_quiz(quiz)
        if not processed_quiz:
            return jsonify({'error': 'Failed to parse quiz data', 'raw_response': quiz}), 500
        # To send quiz back to app
        return jsonify({'quiz': processed_quiz}), 200
    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/test', methods=['GET'])
def run_test():
    # To check if server is working
    return jsonify({'quiz': "test"}), 200

if __name__ == '__main__':
    # To start web server
    port_num = 5000
    print(f"App running on port {port_num}")
    app.run(port=port_num, host="0.0.0.0")
